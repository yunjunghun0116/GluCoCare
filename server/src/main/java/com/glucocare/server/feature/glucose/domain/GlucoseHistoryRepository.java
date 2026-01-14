package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlucoseHistoryRepository extends JpaRepository<GlucoseHistory, Long> {
    List<GlucoseHistory> findAllByPatientIdOrderByDateDesc(Long patientId);

    Optional<GlucoseHistory> findFirstByPatientOrderByDateDesc(Member patient);

    List<GlucoseHistory> findAllByPatientAndDateGreaterThan(Member patient, Long date);
}
