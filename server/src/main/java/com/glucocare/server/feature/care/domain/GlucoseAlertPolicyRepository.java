package com.glucocare.server.feature.care.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlucoseAlertPolicyRepository extends JpaRepository<GlucoseAlertPolicy, Long> {
    Optional<GlucoseAlertPolicy> findByCareGiver(CareGiver careGiver);

    Boolean existsByCareGiver(CareGiver careGiver);
}
