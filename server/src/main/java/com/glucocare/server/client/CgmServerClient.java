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
 * CGM(Continuous Glucose Monitoring) 서버와 통신하는 클라이언트 클래스
 * <p>
 * 이 클래스는 외부 CGM 서버에서 환자의 혈당 데이터를 가져오는 기능을 제공합니다.
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
     * CGM 서버로부터 혈당 데이터 목록을 가져오는 메서드
     * <p>
     * 지정된 CGM 서버 URL에 마지막 동기화 시간 이후의 혈당 데이터를 요청합니다.
     * API 인증을 위해 api-secret 헤더를 사용하며, ngrok 경고 페이지를 건너뛰기 위한
     * 헤더도 포함합니다.
     *
     * @param cgmServerUrl             CGM 서버의 기본 URL
     * @param lastSyncDateMilliseconds 마지막 동기화 날짜 (밀리초 단위 타임스탬프)
     * @return CGM 엔트리 목록 (혈당 기록 데이터)
     * @throws ApplicationException JSON 파싱 실패 시
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
     * CGM 서버 URL을 혈당 데이터 조회 API URL로 변환하는 메서드
     * <p>
     * 기본 CGM 서버 URL에 entries 엔드포인트와 쿼리 파라미터를 추가하여
     * 최종 요청 URL을 생성합니다.
     *
     * @param cgmServerUrl             CGM 서버의 기본 URL
     * @param lastSyncDateMilliseconds 조회 시작 날짜 (밀리초 단위 타임스탬프)
     * @return 완성된 CGM entries API URL
     */
    private String convertCgmServerUrlToEntriesRequest(String cgmServerUrl, Long lastSyncDateMilliseconds) {
        return cgmServerUrl + CGM_ENTRIES_ENDPOINT + "?count=" + MAX_ENTRIES_COUNT + "&find[date][$gt]=" + lastSyncDateMilliseconds;
    }

    /**
     * JSON 문자열을 CgmEntry 리스트로 변환하는 메서드
     * <p>
     * ObjectMapper를 사용하여 CGM 서버로부터 받은 JSON 응답을
     * CgmEntry 객체의 리스트로 역직렬화합니다.
     *
     * @param json CGM 서버로부터 받은 JSON 문자열
     * @param <T>  리스트 요소의 타입
     * @return CgmEntry 객체의 리스트
     * @throws ApplicationException JSON 파싱 중 오류 발생 시
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
