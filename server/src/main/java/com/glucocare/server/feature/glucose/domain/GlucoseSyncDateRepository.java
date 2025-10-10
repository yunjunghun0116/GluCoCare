package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.patient.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlucoseSyncDateRepository extends JpaRepository<GlucoseSyncDate, Long> {
    Optional<GlucoseSyncDate> findFirstByPatientOrderByDateDesc(Patient patient);
}
