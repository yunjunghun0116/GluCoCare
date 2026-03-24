package com.glucocare.server.feature.mission.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByIsActiveTrue();
}
