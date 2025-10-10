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
 * CGM 서버로부터 혈당 데이터를 주기적으로 가져와 저장하는 Use Case 클래스
 * <p>
 * 이 클래스는 스케줄링을 통해 주기적으로 모든 환자들의 CGM 서버에서
 * 최신 혈당 데이터를 가져와 데이터베이스에 저장하는 비즈니스 로직을 처리합니다.
 * 중복 데이터 저장을 방지하기 위해 마지막 저장된 혈당 기록 이후의 데이터만 가져오며,
 * 새로운 혈당 데이터가 저장된 경우 해당 환자의 혈당 히스토리 Redis 캐시를 무효화합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FetchGlucoseHistoryUseCase {

    private final CgmServerClient cgmServerClient;
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final GlucoseSyncDateRepository glucoseSyncDateRepository;
    private final GlucoseHistoryCache glucoseHistoryCache;
    private final ZoneOffset zoneOffset = ZoneOffset.UTC;

    /**
     * 모든 환자의 혈당 데이터를 주기적으로 가져오는 메인 메서드
     * <p>
     * 이 메서드는 1분마다 실행되며, 다음과 같은 과정을 수행합니다:
     * 1. 데이터베이스에서 모든 환자 목록 조회
     * 2. 각 환자별로 CGM 서버에서 최신 혈당 데이터 가져오기
     * 3. 중복되지 않은 새로운 혈당 기록만 데이터베이스에 저장
     *
     * @Scheduled 어노테이션을 통해 60초(60000ms)마다 자동 실행됩니다.
     */
    @Scheduled(fixedRate = 60000)
    public void execute() {
        var patients = patientRepository.findAll();

        for (var patient : patients) {
            savePatientsGlucoseHistory(patient);
        }
    }

    private void savePatientsGlucoseHistory(Patient patient) {
        var lastSyncMilliseconds = getMilliseconds(patient);
        var entries = cgmServerClient.getCgmEntries(patient.getCgmServerUrl(), lastSyncMilliseconds);
        var isGlucoseHistoryChanged = false;
        var validationSet = getValidationSet(patient, lastSyncMilliseconds);

        for (var entry : entries) {
            if (validationSet.contains(entry.date())) continue;
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

    private Set<Long> getValidationSet(Patient patient, Long lastSyncMilliseconds) {
        var validationSet = new HashSet<Long>();

        var histories = glucoseHistoryRepository.findAllByPatientAndDateGreaterThan(patient, lastSyncMilliseconds);

        for (var history : histories) {
            validationSet.add(history.getDate());
        }

        return validationSet;
    }

    private Long getMilliseconds(Patient patient) {
        var lastSyncDate = glucoseSyncDateRepository.findFirstByPatientOrderByDateDesc(patient);
        if (lastSyncDate.isPresent()) {
            return lastSyncDate.get()
                               .getDate()
                               .atStartOfDay(zoneOffset)
                               .toInstant()
                               .toEpochMilli();
        }
        var defaultDate = LocalDate.of(2025, 1, 1);
        return defaultDate.atStartOfDay(zoneOffset)
                          .toInstant()
                          .toEpochMilli();
    }

    private Boolean canAddSyncDate(Long milliseconds) {
        var todayDate = LocalDate.now(zoneOffset);
        var todayMilliseconds = todayDate.atStartOfDay(zoneOffset)
                                         .toInstant()
                                         .toEpochMilli();
        return todayMilliseconds > milliseconds;
    }
}
