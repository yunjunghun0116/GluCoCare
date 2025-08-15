package com.glucocare.server.feature.glucosehistory.domain;

import com.glucocare.server.feature.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlucoseHistoryRepository extends JpaRepository<GlucoseHistory, Long> {
    List<GlucoseHistory> findAllByPatientOrderByDateDesc(Patient patient);

    Optional<GlucoseHistory> findFirstByPatientOrderByDateDesc(Patient patient);
}
