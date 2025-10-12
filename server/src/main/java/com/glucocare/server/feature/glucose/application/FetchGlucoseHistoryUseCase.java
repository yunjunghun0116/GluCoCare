package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.client.CgmServerClient;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.domain.GlucoseSyncDate;
import com.glucocare.server.feature.glucose.domain.GlucoseSyncDateRepository;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

/**
 * 혈당 데이터 동기화 Use Case
 * <p>
 * CGM 서버로부터 혈당 데이터를 주기적으로 가져와 저장합니다.
 * 스케줄링을 통해 1분마다 모든 환자들의 CGM 서버에서 최신 혈당 데이터를 가져옵니다.
 * 중복 저장을 방지하기 위해 마지막 동기화 날짜 이후의 데이터만 가져오며,
 * 새로운 혈당 데이터가 저장되면 해당 환자의 Redis 캐시를 무효화합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FetchGlucoseHistoryUseCase {

    private static final LocalDate DEFAULT_SYNC_START_DATE = LocalDate.of(2025, 1, 1);
    private final CgmServerClient cgmServerClient;
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final GlucoseSyncDateRepository glucoseSyncDateRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;
    private final ZoneOffset zoneOffset = ZoneOffset.UTC;

    /**
     * 모든 환자의 혈당 데이터 주기적 동기화
     * <p>
     * 비즈니스 로직 순서:
     * 1. 데이터베이스에서 모든 환자 목록 조회
     * 2. 각 환자별로 혈당 데이터 동기화 수행
     * 3. CGM 서버에서 최신 혈당 데이터 가져오기
     * 4. 중복되지 않은 새로운 혈당 기록만 데이터베이스에 저장
     * 5. 새로운 데이터가 저장된 경우 해당 환자의 캐시 무효화
     *
     * @Scheduled 어노테이션을 통해 60초(60000ms)마다 자동 실행
     */
    @Scheduled(fixedRate = 60000)
    public void execute() {
        var patients = patientRepository.findAll();

        for (var patient : patients) {
            savePatientsGlucoseHistory(patient);
        }
    }

    /**
     * 환자의 혈당 데이터 동기화 및 저장
     * <p>
     * 처리 단계:
     * 1. 마지막 동기화 날짜를 밀리초로 계산
     * 2. CGM 서버에서 마지막 동기화 시점 이후의 혈당 데이터 조회
     * 3. 기존 데이터베이스에 저장된 날짜 집합 생성 (중복 체크용)
     * 4. 각 혈당 데이터에 대해:
     *    - 이미 존재하는 날짜면 건너뛰기
     *    - 새로운 데이터면 데이터베이스에 저장
     * 5. 새로운 데이터가 저장되었으면:
     *    - 해당 환자의 혈당 기록 캐시 무효화
     *    - 오늘 날짜보다 이전 데이터를 동기화했으면 동기화 날짜 기록
     *
     * @param patient 동기화할 환자 엔티티
     */
    private void savePatientsGlucoseHistory(Patient patient) {
        var lastSyncMilliseconds = getMilliseconds(patient);
        var entries = cgmServerClient.getCgmEntries(patient.getCgmServerUrl(), lastSyncMilliseconds);
        var isGlucoseHistoryChanged = false;
        var existingDateSet = getExistingDateSet(patient, lastSyncMilliseconds);

        for (var entry : entries) {
            if (existingDateSet.contains(entry.date())) continue;
            var glucoseHistory = new GlucoseHistory(patient, entry.sgv(), entry.date());
            glucoseHistoryRepository.save(glucoseHistory);
            isGlucoseHistoryChanged = true;
        }

        if (isGlucoseHistoryChanged) {
            glucoseHistoryCache.clearByPatientId(patient.getId());

            if (canAddSyncDate(lastSyncMilliseconds)) {
                var todayDate = LocalDate.now(zoneOffset);
                var newSyncDate = new GlucoseSyncDate(patient, todayDate);
                glucoseSyncDateRepository.save(newSyncDate);
            }
        }
    }

    /**
     * 기존 혈당 기록의 날짜 집합 생성
     * <p>
     * 처리 단계:
     * 1. 빈 HashSet 생성
     * 2. 마지막 동기화 시점 이후의 모든 혈당 기록 조회
     * 3. 각 혈당 기록의 날짜(밀리초)를 HashSet에 추가
     * 4. 생성된 HashSet 반환 (중복 체크용)
     *
     * @param patient              조회할 환자 엔티티
     * @param lastSyncMilliseconds 마지막 동기화 시점 (밀리초)
     * @return 기존 혈당 기록 날짜 집합 (밀리초 단위)
     */
    private Set<Long> getExistingDateSet(Patient patient, Long lastSyncMilliseconds) {
        var existingDateSet = new HashSet<Long>();

        var histories = glucoseHistoryRepository.findAllByPatientAndDateGreaterThan(patient, lastSyncMilliseconds);

        for (var history : histories) {
            existingDateSet.add(history.getDate());
        }

        return existingDateSet;
    }

    /**
     * 마지막 동기화 시점 계산 (밀리초)
     * <p>
     * 처리 단계:
     * 1. 환자의 마지막 동기화 날짜 조회
     * 2. 동기화 날짜가 있으면:
     *    - 해당 날짜의 00시 00분을 UTC 기준 밀리초로 변환하여 반환
     * 3. 동기화 날짜가 없으면:
     *    - 기본 시작 날짜(2025-01-01)의 00시 00분을 UTC 기준 밀리초로 변환하여 반환
     *
     * @param patient 조회할 환자 엔티티
     * @return 마지막 동기화 시점 또는 기본 시작 시점 (밀리초)
     */
    private Long getMilliseconds(Patient patient) {
        var lastSyncDate = glucoseSyncDateRepository.findFirstByPatientOrderByDateDesc(patient);
        if (lastSyncDate.isPresent()) {
            return lastSyncDate.get()
                               .getDate()
                               .atStartOfDay(zoneOffset)
                               .toInstant()
                               .toEpochMilli();
        }
        return DEFAULT_SYNC_START_DATE.atStartOfDay(zoneOffset)
                                      .toInstant()
                                      .toEpochMilli();
    }

    /**
     * 동기화 날짜 기록 가능 여부 확인
     * <p>
     * 처리 단계:
     * 1. 오늘 날짜의 00시 00분을 UTC 기준 밀리초로 변환
     * 2. 오늘 밀리초가 마지막 동기화 밀리초보다 큰지 확인
     * 3. 크면 true (과거 데이터 동기화 완료, 기록 가능), 작거나 같으면 false (오늘 데이터, 기록 불가)
     *
     * @param milliseconds 마지막 동기화 시점 (밀리초)
     * @return true: 동기화 날짜 기록 가능, false: 기록 불가
     */
    private Boolean canAddSyncDate(Long milliseconds) {
        var todayDate = LocalDate.now(zoneOffset);
        var todayMilliseconds = todayDate.atStartOfDay(zoneOffset)
                                         .toInstant()
                                         .toEpochMilli();
        return todayMilliseconds > milliseconds;
    }
}
