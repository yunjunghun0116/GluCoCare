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

@Service
@RequiredArgsConstructor
@Transactional
public class FetchGlucoseHistory {

    private final CgmServerClient cgmServerClient;
    private final PatientRepository patientRepository;
    private final GlucoseHistoryRepository glucoseHistoryRepository;

    @Scheduled(fixedRate = 60000)
    public void execute() {
        var patients = patientRepository.findAll();

        for (var patient : patients) {
            savePatientsGlucoseHistory(patient);
        }
    }


    private void savePatientsGlucoseHistory(Patient patient) {
        var entries = cgmServerClient.getCgmEntries(patient.getCgmServerUrl());
        var recentHistory = glucoseHistoryRepository.findFirstByPatientOrderByDateDesc(patient);
        for (var entry : entries) {
            if (isEnd(entry, recentHistory)) break;
            var glucoseHistory = new GlucoseHistory(patient, entry.sgv(), entry.date());
            glucoseHistoryRepository.save(glucoseHistory);
        }
    }

    private boolean isEnd(CgmEntry entry, Optional<GlucoseHistory> recentHistory) {
        if (recentHistory.isEmpty()) return false;
        var history = recentHistory.get();
        return entry.date() <= history.getDate();
    }
}
