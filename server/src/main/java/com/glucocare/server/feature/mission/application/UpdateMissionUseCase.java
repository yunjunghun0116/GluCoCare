package com.glucocare.server.feature.mission.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.mission.domain.MissionRepository;
import com.glucocare.server.feature.mission.dto.UpdateMissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMissionUseCase {

    private final MissionRepository missionRepository;

    public void execute(Long id, UpdateMissionRequest request) {
        var mission = missionRepository.findById(id)
                                       .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        mission.update(request.title(), request.description(), request.threshold(), request.rewardPoint(), request.isActive());
    }
}
