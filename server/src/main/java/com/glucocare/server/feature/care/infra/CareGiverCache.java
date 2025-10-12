package com.glucocare.server.feature.care.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CareGiverCache {
    private static final String KEY_PREFIX = "care:giver:";
    private static final Long CACHE_REMAINED_HOURS = 6L;
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean existsByMemberIdAndPatientId(Long memberId, Long patientId) {
        var key = KEY_PREFIX + memberId + ":" + patientId;
        return redisTemplate.hasKey(key);
    }

    public void cacheRelation(Long memberId, Long patientId) {
        var key = KEY_PREFIX + memberId + ":" + patientId;

        var valueOps = redisTemplate.opsForValue();
        valueOps.set(key, "EXISTS", Duration.ofHours(CACHE_REMAINED_HOURS));
    }
}
