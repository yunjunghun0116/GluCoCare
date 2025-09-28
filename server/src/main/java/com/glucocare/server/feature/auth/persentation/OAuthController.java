package com.glucocare.server.feature.auth.persentation;

import com.glucocare.server.client.FitbitClient;
import com.glucocare.server.client.dto.FitbitOAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    private final FitbitClient fitbitClient;

    @GetMapping("/authorize/fitbit")
    public ResponseEntity<Void> authorizeFitbit() {
        var authorizeUrl = fitbitClient.generateAuthorizeUrl();
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(authorizeUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/callback/fitbit")
    public ResponseEntity<FitbitOAuthResponse> callbackFitbit(@RequestParam String code) {
        var response = fitbitClient.getOAuthResponse(code);
        return ResponseEntity.ok(response);
    }
}
