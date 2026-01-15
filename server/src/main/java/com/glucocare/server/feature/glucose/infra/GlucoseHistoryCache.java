package com.glucocare.server.feature.glucose.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlucoseHistoryCache {
    private static final String KEY_PREFIX = "glucose:history:";
    private static final Long CACHE_REMAINED_HOURS = 1L;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Boolean existsByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        return redisTemplate.hasKey(key);
    }

    public List<ReadGlucoseHistoryResponse> readAllByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        var json = redisTemplate.opsForValue()
                                .get(key);
        if (json == null) return List.of();
        try {
            var type = objectMapper.getTypeFactory()
                                   .constructCollectionType(List.class, ReadGlucoseHistoryResponse.class);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void createGlucoseHistories(Long patientId, List<ReadGlucoseHistoryResponse> histories) {
        var key = KEY_PREFIX + patientId;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }

        try {
            var value = objectMapper.writeValueAsString(histories);
            redisTemplate.opsForValue()
                         .set(key, value, Duration.ofHours(CACHE_REMAINED_HOURS));
        } catch (JsonProcessingException exception) {
            throw new ApplicationException(ErrorMessage.INVALID_CONVERT_REQUEST);
        }
    }

    public void clearByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        redisTemplate.delete(key);
    }
}
