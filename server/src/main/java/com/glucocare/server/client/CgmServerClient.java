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

@Slf4j
@Component
@RequiredArgsConstructor
public class CgmServerClient {

    private static final String CGM_ENTRIES_ENDPOINT = "/api/v1/entries.json";
    private static final int MAX_ENTRIES_COUNT = 99999;
    private final RestClient restClient;
    private final CgmProperties cgmProperties;
    private final ObjectMapper objectMapper;

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

    private String convertCgmServerUrlToEntriesRequest(String cgmServerUrl, Long lastSyncDateMilliseconds) {
        return cgmServerUrl + CGM_ENTRIES_ENDPOINT + "?count=" + MAX_ENTRIES_COUNT + "&find[date][$gt]=" + lastSyncDateMilliseconds;
    }

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
