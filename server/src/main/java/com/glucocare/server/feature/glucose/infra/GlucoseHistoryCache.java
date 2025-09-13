package com.glucocare.server.feature.glucose.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GlucoseHistoryCache {
    private static final String KEY_PREFIX = "glucose:history:";
    private static final Long CACHE_REMAINED_HOURS = 1L;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public Boolean existsByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        return redisTemplate.hasKey(key);
    }

    public List<GlucoseHistory> readAllByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        var listOps = redisTemplate.opsForList();
        var raw = listOps.range(key, 0, -1);
        if (raw == null) return List.of();
        return raw.stream()
                  .map((obj) -> objectMapper.convertValue(obj, GlucoseHistory.class))
                  .toList();
    }

    public void createGlucoseHistories(Long patientId, List<GlucoseHistory> histories) {
        var key = KEY_PREFIX + patientId;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
        }

        var listOps = redisTemplate.opsForList();
        listOps.rightPushAll(key, histories.toArray());
        redisTemplate.expire(key, CACHE_REMAINED_HOURS, TimeUnit.HOURS);
    }

    public void clearByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        redisTemplate.delete(key);
    }
}
