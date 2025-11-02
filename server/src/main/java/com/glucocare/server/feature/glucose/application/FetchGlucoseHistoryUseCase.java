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

    private Set<Long> getExistingDateSet(Patient patient, Long lastSyncMilliseconds) {
        var existingDateSet = new HashSet<Long>();

        var histories = glucoseHistoryRepository.findAllByPatientAndDateGreaterThan(patient, lastSyncMilliseconds);

        for (var history : histories) {
            existingDateSet.add(history.getDate());
        }

        return existingDateSet;
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
        return DEFAULT_SYNC_START_DATE.atStartOfDay(zoneOffset)
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
