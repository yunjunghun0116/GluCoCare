package com.glucocare.server.feature.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByNameAndId(String name, Long id);
}
