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

/**
 * Fitbit OAuth 통신 클라이언트
 * <p>
 * Fitbit OAuth 2.0 인증을 처리합니다.
 * 인증 URL 생성 및 Access Token 발급 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FitbitClient {

    private final RestClient restClient;
    private final FitbitProperties fitbitProperties;
    private final ObjectMapper objectMapper;

    /**
     * Fitbit OAuth 인증 URL 생성
     * <p>
     * 처리 단계:
     * 1. Fitbit OAuth 인증 엔드포인트에 클라이언트 ID, scope, redirect URI를 포함한 URL 생성
     * 2. 생성된 URL 반환 (사용자가 이 URL로 접속하여 Fitbit 계정 인증)
     *
     * @return Fitbit OAuth 인증 URL
     */
    public String generateAuthorizeUrl() {
        return "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=" + fitbitProperties.clientId() + "&scope=activity+heartrate+respiratory_rate+sleep+temperature&redirect_uri=" + fitbitProperties.redirectUri();
    }

    /**
     * Fitbit OAuth Access Token 발급
     * <p>
     * 처리 단계:
     * 1. 클라이언트 ID와 Secret을 Base64로 인코딩하여 Basic 인증 헤더 생성
     * 2. 인증 코드, grant_type, redirect URI를 포함한 폼 데이터 생성
     * 3. Fitbit Token 엔드포인트로 POST 요청 전송
     * 4. 응답 JSON을 FitbitOAuthResponse로 변환하여 반환
     *
     * @param code Fitbit OAuth 인증 코드
     * @return Access Token, Refresh Token 등을 포함한 OAuth 응답
     * @throws ApplicationException JSON 파싱 실패 시 (INVALID_CONVERT_REQUEST)
     */
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

    /**
     * JSON 문자열을 FitbitOAuthResponse로 변환
     * <p>
     * 처리 단계:
     * 1. ObjectMapper로 JSON 문자열을 FitbitOAuthResponse 객체로 역직렬화
     * 2. 역직렬화된 객체 반환
     * 3. 파싱 실패 시 예외 발생
     *
     * @param json Fitbit OAuth 서버로부터 받은 JSON 문자열
     * @return FitbitOAuthResponse 객체
     * @throws ApplicationException JSON 파싱 실패 시 (INVALID_CONVERT_REQUEST)
     */
    private FitbitOAuthResponse convertJsonToResponse(String json) {
        try {
            return objectMapper.readValue(json, FitbitOAuthResponse.class);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(ErrorMessage.INVALID_CONVERT_REQUEST);
        }
    }
}
