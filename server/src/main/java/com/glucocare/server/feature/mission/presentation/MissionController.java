package com.glucocare.server.feature.mission.presentation;

import com.glucocare.server.feature.mission.application.CreateMissionUseCase;
import com.glucocare.server.feature.mission.application.UpdateMissionUseCase;
import com.glucocare.server.feature.mission.dto.CreateMissionRequest;
import com.glucocare.server.feature.mission.dto.UpdateMissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/missions")
public class MissionController {
    private final CreateMissionUseCase createMissionUseCase;
    private final UpdateMissionUseCase updateMissionUseCase;

    @PostMapping
    public ResponseEntity<Void> createMission(@RequestBody CreateMissionRequest request) {
        createMissionUseCase.execute(request);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateMission(@PathVariable Long id, @RequestBody UpdateMissionRequest request) {
        updateMissionUseCase.execute(id, request);
        return ResponseEntity.ok()
                             .build();
    }
}
