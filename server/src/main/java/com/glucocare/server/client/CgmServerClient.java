package com.glucocare.server.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.client.dto.CgmEntry;
import com.glucocare.server.config.properties.CgmProperties;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

/**
 * CGM 서버 통신 클라이언트
 * <p>
 * 외부 CGM 서버에서 환자의 혈당 데이터를 가져옵니다.
 * Nightscout 등의 CGM 서버 API와 통신하여 혈당 기록을 조회합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CgmServerClient {

    private static final String CGM_ENTRIES_ENDPOINT = "/api/v1/entries.json";
    private static final int MAX_ENTRIES_COUNT = 99999;
    private final RestClient restClient;
    private final CgmProperties cgmProperties;
    private final ObjectMapper objectMapper;

    /**
     * CGM 서버로부터 혈당 데이터 조회
     * <p>
     * 처리 단계:
     * 1. CGM 서버 URL과 마지막 동기화 시점으로 요청 URL 생성
     * 2. RestClient로 GET 요청 전송 (api-secret, ngrok-skip-browser-warning 헤더 포함)
     * 3. 응답 JSON 문자열을 CgmEntry 리스트로 변환
     * 4. 변환된 리스트 반환
     *
     * @param cgmServerUrl             CGM 서버의 기본 URL
     * @param lastSyncDateMilliseconds 마지막 동기화 시점 (밀리초)
     * @return 혈당 기록 데이터 리스트
     * @throws ApplicationException JSON 파싱 실패 시 (INVALID_CONVERT_REQUEST)
     */
    public List<CgmEntry> getCgmEntries(String cgmServerUrl, Long lastSyncDateMilliseconds) {
        var response = restClient.get()
                                 .uri(URI.create(convertCgmServerUrlToEntriesRequest(cgmServerUrl, lastSyncDateMilliseconds)))
                                 .accept(MediaType.APPLICATION_JSON)
                                 .headers(headers -> {
                                     headers.add("api-secret", cgmProperties.apiSecret());
                                     headers.add("ngrok-skip-browser-warning", "true");
                                     headers.add("Content-Type", "application/json");
                                 })
                                 .retrieve()
                                 .body(String.class);
        return convertJsonToList(response);
    }

    /**
     * CGM 서버 URL을 혈당 데이터 조회 API URL로 변환
     * <p>
     * 처리 단계:
     * 1. CGM 서버 기본 URL에 entries 엔드포인트 추가
     * 2. count 쿼리 파라미터 추가 (최대 99999개)
     * 3. find[date][$gt] 쿼리 파라미터 추가 (마지막 동기화 시점 이후)
     * 4. 완성된 URL 반환
     *
     * @param cgmServerUrl             CGM 서버의 기본 URL
     * @param lastSyncDateMilliseconds 조회 시작 시점 (밀리초)
     * @return 완성된 CGM entries API URL
     */
    private String convertCgmServerUrlToEntriesRequest(String cgmServerUrl, Long lastSyncDateMilliseconds) {
        return cgmServerUrl + CGM_ENTRIES_ENDPOINT + "?count=" + MAX_ENTRIES_COUNT + "&find[date][$gt]=" + lastSyncDateMilliseconds;
    }

    /**
     * JSON 문자열을 CgmEntry 리스트로 변환
     * <p>
     * 처리 단계:
     * 1. ObjectMapper로 List<CgmEntry> 타입 생성
     * 2. JSON 문자열을 List<CgmEntry>로 역직렬화
     * 3. 역직렬화된 리스트 반환
     * 4. 파싱 실패 시 예외 발생
     *
     * @param json CGM 서버로부터 받은 JSON 문자열
     * @param <T>  리스트 요소의 타입
     * @return CgmEntry 객체의 리스트
     * @throws ApplicationException JSON 파싱 실패 시 (INVALID_CONVERT_REQUEST)
     */
    private <T> List<T> convertJsonToList(String json) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                                        .constructCollectionType(List.class, CgmEntry.class);
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new ApplicationException(ErrorMessage.INVALID_CONVERT_REQUEST);
        }
    }
}
