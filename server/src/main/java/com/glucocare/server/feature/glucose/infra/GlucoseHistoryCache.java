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

/**
 * Redis를 활용한 혈당 기록 캐시 서비스 클래스
 * <p>
 * 이 클래스는 환자별 혈당 기록 데이터를 Redis에 캐시하여 데이터베이스 조회를 최적화합니다.
 * 각 환자의 혈당 기록은 "glucose:history:{patientId}" 키 형태로 저장되며, 1시간의 TTL(Time To Live)을 갖습니다.
 * Redis List 자료구조를 사용하여 혈당 기록들을 순서를 유지하면서 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class GlucoseHistoryCache {
    private static final String KEY_PREFIX = "glucose:history:";
    private static final Long CACHE_REMAINED_HOURS = 1L;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 특정 환자의 혈당 기록 캐시가 존재하는지 확인하는 메서드
     * <p>
     * 주어진 환자 ID에 해당하는 캐시 키가 Redis에 존재하는지 확인합니다.
     * 캐시가 존재하면 데이터베이스 조회 없이 캐시에서 데이터를 가져올 수 있습니다.
     *
     * @param patientId 캐시 존재 여부를 확인할 환자의 ID
     * @return 캐시가 존재하면 true, 존재하지 않으면 false
     */
    public Boolean existsByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        return redisTemplate.hasKey(key);
    }

    /**
     * 특정 환자의 모든 혈당 기록을 캐시에서 조회하는 메서드
     * <p>
     * Redis List에서 환자의 모든 혈당 기록을 조회하고 GlucoseHistory 객체 리스트로 변환합니다.
     * ObjectMapper를 사용하여 Redis에 저장된 JSON 형태의 데이터를 Java 객체로 역직렬화합니다.
     *
     * @param patientId 혈당 기록을 조회할 환자의 ID
     * @return 해당 환자의 모든 혈당 기록 리스트 (캐시에 데이터가 없으면 빈 리스트)
     */
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

    /**
     * 특정 환자의 혈당 기록 리스트를 캐시에 저장하는 메서드
     * <p>
     * 환자의 혈당 기록 리스트를 Redis List에 저장합니다.
     * 기존 캐시가 있다면 먼저 삭제한 후 새로운 데이터를 저장하며,
     * 1시간의 TTL을 설정하여 자동으로 만료되도록 합니다.
     *
     * @param patientId 혈당 기록을 저장할 환자의 ID
     * @param histories 캐시에 저장할 혈당 기록 리스트
     */
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

    /**
     * 특정 환자의 혈당 기록 캐시를 무효화하는 메서드
     * <p>
     * 환자의 새로운 혈당 데이터가 추가되었을 때 캐시된 데이터가 최신 상태를 반영하지 못할 수 있으므로,
     * 해당 환자의 캐시를 삭제하여 다음 조회 시 데이터베이스에서 최신 데이터를 가져오도록 합니다.
     * 주로 FetchGlucoseHistoryUseCase에서 새로운 혈당 데이터 저장 후 호출됩니다.
     *
     * @param patientId 캐시를 무효화할 환자의 ID
     */
    public void clearByPatientId(Long patientId) {
        var key = KEY_PREFIX + patientId;
        redisTemplate.delete(key);
    }
}
