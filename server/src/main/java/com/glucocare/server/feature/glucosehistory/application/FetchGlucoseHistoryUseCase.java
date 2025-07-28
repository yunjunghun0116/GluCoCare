package com.glucocare.server.feature.glucosehistory.application;

import com.glucocare.server.client.CgmServerClient;
import com.glucocare.server.client.dto.CgmEntry;
import com.glucocare.server.feature.glucosehistory.domain.GlucoseHistory;
import com.glucocare.server.feature.glucosehistory.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.feature.patient.domain.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * CGM 서버에서 혈당 데이터를 주기적으로 가져와 저장하는 Use Case 클래스
 * 
 * 이 클래스는 등록된 모든 환자의 CGM 서버에서 혈당 데이터를 주기적으로 가져와서
 * 데이터베이스에 저장하는 배치 작업을 수행합니다. 스케줄링을 통해 자동으로 실행됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FetchGlucoseHistoryUseCase {

    private final CgmServerClient cgmServerClient;
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;

    /**
     * 모든 환자의 혈당 데이터를 주기적으로 가져와 저장하는 메인 메서드
     * 
     * 이 메서드는 1분마다 자동으로 실행되며, 다음과 같은 과정을 수행합니다:
     * 1. 데이터베이스에 등록된 모든 환자 조회
     * 2. 각 환자의 CGM 서버에서 혈당 데이터 가져오기
     * 3. 중복되지 않는 새로운 데이터만 데이터베이스에 저장
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
     * 
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 환자의 CGM 서버에서 혈당 데이터 목록 조회
     * 2. 데이터베이스에서 해당 환자의 가장 최근 혈당 기록 조회
     * 3. 중복되지 않는 새로운 데이터만 데이터베이스에 저장
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
     * CGM 데이터 저장을 중단할지 판단하는 메서드
     * 
     * 이 메서드는 현재 처리 중인 CGM 엔트리의 날짜가 데이터베이스에 저장된
     * 가장 최근 혈당 기록의 날짜보다 이전인지 확인합니다.
     * 중복 저장을 방지하기 위해 이미 저장된 데이터보다 오래된 데이터는 저장하지 않습니다.
     * 
     * @param entry 현재 처리 중인 CGM 데이터 엔트리
     * @param recentHistory 데이터베이스에서 조회한 가장 최근 혈당 기록 (Optional)
     * @return 저장을 중단해야 하면 true, 계속 저장해야 하면 false
     */
    private boolean isEnd(CgmEntry entry, Optional<GlucoseHistory> recentHistory) {
        if (recentHistory.isEmpty()) return false;
        var history = recentHistory.get();
        return entry.date() <= history.getDate();
    }
}
