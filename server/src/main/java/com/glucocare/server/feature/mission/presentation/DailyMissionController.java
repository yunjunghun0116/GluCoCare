package com.glucocare.server.feature.mission.presentation;

import com.glucocare.server.feature.mission.application.CompleteDailyMissionUseCase;
import com.glucocare.server.feature.mission.application.ReadTodayDailyMissionUseCase;
import com.glucocare.server.feature.mission.dto.DailyMissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/daily-missions")
public class DailyMissionController {
    private final CompleteDailyMissionUseCase completeDailyMissionUseCase;
    private final ReadTodayDailyMissionUseCase readTodayDailyMissionUseCase;

    @PostMapping("/{id}")
    public ResponseEntity<Void> completeDailyMission(@PathVariable Long id, @AuthenticationPrincipal Long memberId) {
        completeDailyMissionUseCase.execute(id, memberId);
        return ResponseEntity.ok()
                             .build();
    }

    @GetMapping
    public ResponseEntity<List<DailyMissionResponse>> readTodayDailyMission(@AuthenticationPrincipal Long memberId) {
        var response = readTodayDailyMissionUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }
}
