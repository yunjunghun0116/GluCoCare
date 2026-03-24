package com.glucocare.server.feature.mission.application;

import com.glucocare.server.feature.mission.domain.Mission;
import com.glucocare.server.feature.mission.domain.MissionRepository;
import com.glucocare.server.feature.mission.dto.CreateMissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateMissionUseCase {

    private final MissionRepository missionRepository;

    public void execute(CreateMissionRequest request) {
        var mission = new Mission(request.title(), request.description(), request.missionType(), request.threshold(), request.rewardPoint());
        missionRepository.save(mission);
    }
}
