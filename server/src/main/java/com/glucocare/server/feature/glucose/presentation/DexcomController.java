package com.glucocare.server.feature.glucose.presentation;

import com.glucocare.server.feature.glucose.application.DexcomCreateGlucoseHistoryUseCase;
import com.glucocare.server.feature.glucose.dto.DexcomGlucoseRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/{patientId}/api/v1")
public class DexcomController {
    private final DexcomCreateGlucoseHistoryUseCase createGlucoseHistoryUseCase;

    @PostMapping("/entries")
    public ResponseEntity<Void> create(HttpServletRequest request, @PathVariable Long patientId, @RequestBody List<DexcomGlucoseRequest> entries) {
        var accessCode = extractAccessCode(request);
        createGlucoseHistoryUseCase.execute(patientId, accessCode, entries);
        return ResponseEntity.ok()
                             .build();
    }

    @PostMapping("/devicestatus")
    public ResponseEntity<Void> deviceStatus(@PathVariable Long patientId) {
        return ResponseEntity.noContent()
                             .build();
    }

    @GetMapping("/treatments")
    public ResponseEntity<List<Object>> treatments(@PathVariable Long patientId) {
        var emptyList = List.of();
        return ResponseEntity.ok(emptyList);
    }

    @PutMapping("/treatments")
    public ResponseEntity<Void> uploadTreatments(@PathVariable Long patientId) {
        return ResponseEntity.noContent()
                             .build();
    }

    private String extractAccessCode(HttpServletRequest request) {
        var auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Basic ")) return null;

        var base64 = auth.substring("Basic ".length());
        var decoded = new String(java.util.Base64.getDecoder()
                                                 .decode(base64), java.nio.charset.StandardCharsets.UTF_8);
        var idx = decoded.indexOf(':');
        return (decoded.indexOf(':') >= 0) ? decoded.substring(0, idx) : decoded;
    }
}
