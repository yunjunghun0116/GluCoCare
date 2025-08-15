package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.client.CgmServerClient;
import com.glucocare.server.client.dto.CgmEntry;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * CGM 서버로부터 혈당 데이터를 주기적으로 가져오는 Use Case 클래스
 * <p>
 * 이 클래스는 스케줄링을 통해 주기적으로 모든 환자들의 CGM 서버에서
 * 최신 혈당 데이터를 가져와 데이터베이스에 저장하는 비즈니스 로직을 처리합니다.
 * 중복 데이터 저장을 방지하기 위해 마지막 저장된 혈당 기록 이후의 데이터만 가져옵니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FetchGlucoseHistoryUseCase {

    private final CgmServerClient cgmServerClient;
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;

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

    /**
     * 특정 환자의 혈당 데이터를 CGM 서버에서 가져와 저장하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 환자의 CGM 서버 URL을 통해 혈당 데이터 목록 조회
     * 2. 해당 환자의 가장 최근 저장된 혈당 기록 조회
     * 3. CGM 서버에서 가져온 데이터 중 최근 기록 이후의 새로운 데이터만 필터링
     * 4. 새로운 혈당 기록을 데이터베이스에 저장
     *
     * @param patient 혈당 데이터를 저장할 환자 엔티티
     */
    private void savePatientsGlucoseHistory(Patient patient) {
        var entries = cgmServerClient.getCgmEntries(patient.getCgmServerUrl());
        var recentHistory = glucoseHistoryRepository.findFirstByPatientOrderByDateDesc(patient);
        for (var entry : entries) {
            if (isEnd(entry, recentHistory)) break;
            var glucoseHistory = new GlucoseHistory(patient, entry.sgv(), entry.date());
            glucoseHistoryRepository.save(glucoseHistory);
        }
    }

    /**
     * CGM 데이터 처리 종료 조건을 판단하는 메서드
     * <p>
     * 이 메서드는 중복 데이터 저장을 방지하기 위해 사용됩니다.
     * CGM 서버에서 가져온 혈당 데이터가 이미 데이터베이스에 저장된
     * 가장 최근 기록보다 오래된 경우 처리를 중단합니다.
     *
     * @param entry         CGM 서버에서 가져온 혈당 데이터 엔트리
     * @param recentHistory 데이터베이스에 저장된 가장 최근 혈당 기록 (Optional)
     * @return 처리를 중단해야 하면 true, 계속 처리하면 false
     */
    private boolean isEnd(CgmEntry entry, Optional<GlucoseHistory> recentHistory) {
        if (recentHistory.isEmpty()) return false;
        var history = recentHistory.get();
        return entry.date() <= history.getDate();
    }
}
