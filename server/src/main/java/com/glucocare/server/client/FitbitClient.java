package com.glucocare.server.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.client.dto.FitbitOAuthResponse;
import com.glucocare.server.config.properties.FitbitProperties;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class FitbitClient {

    private final RestClient restClient;
    private final FitbitProperties fitbitProperties;
    private final ObjectMapper objectMapper;

    public String generateAuthorizeUrl() {
        return "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=" + fitbitProperties.clientId() + "&scope=activity+heartrate+respiratory_rate+sleep+temperature&redirect_uri=" + fitbitProperties.redirectUri();
    }

    public FitbitOAuthResponse getOAuthResponse(String code) {
        String authorization = fitbitProperties.clientId() + ":" + fitbitProperties.clientSecret();
        String basic = Base64.getEncoder()
                             .encodeToString(authorization.getBytes(StandardCharsets.UTF_8));

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("client_id", fitbitProperties.clientId());
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", fitbitProperties.redirectUri());

        var response = restClient.post()
                                 .uri(URI.create("https://api.fitbit.com/oauth2/token"))
                                 .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                 .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                                 .body(body)
                                 .retrieve()
                                 .body(String.class);

        return convertJsonToResponse(response);
    }

    private FitbitOAuthResponse convertJsonToResponse(String json) {
        try {
            return objectMapper.readValue(json, FitbitOAuthResponse.class);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(ErrorMessage.INVALID_CONVERT_REQUEST);
        }
    }
}
