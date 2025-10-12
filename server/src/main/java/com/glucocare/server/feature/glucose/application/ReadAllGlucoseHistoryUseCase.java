package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.infra.CareGiverCache;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.domain.GlucoseHistoryRepository;
import com.glucocare.server.feature.glucose.dto.ReadGlucoseHistoryResponse;
import com.glucocare.server.feature.glucose.infra.GlucoseHistoryCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 혈당 기록 전체 조회 Use Case
 * <p>
 * 특정 환자의 모든 혈당 기록을 조회합니다.
 * 권한 검증을 통해 해당 회원이 환자의 간병인인지 확인한 후 데이터를 제공합니다.
 * 성능 최적화를 위해 Redis 캐시를 활용하여, 캐시된 데이터가 있으면 데이터베이스 조회 없이 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllGlucoseHistoryUseCase {
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;
    private final CareGiverCache careGiverCache;
    private final GlucoseHistoryCache glucoseHistoryCache;

    /**
     * 혈당 기록 전체 조회 (권한 검증 및 캐시 활용)
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원이 해당 환자의 간병인인지 권한 검증
     * 2. Redis 캐시에 환자의 혈당 데이터가 있는지 확인
     * 3. 캐시된 데이터가 있으면 캐시에서 반환
     * 4. 캐시된 데이터가 없으면 데이터베이스에서 날짜 역순으로 조회
     * 5. 조회된 혈당 기록들을 응답 DTO 리스트로 변환
     * 6. 변환된 데이터를 캐시에 저장
     * 7. 응답 DTO 리스트 반환
     *
     * @param memberId  조회를 요청한 회원의 ID
     * @param patientId 조회할 환자의 ID
     * @return 혈당 기록 ID, 날짜, 혈당 수치를 포함한 응답 리스트 (날짜 역순 정렬)
     * @throws ApplicationException 간병인 권한이 없는 경우 (INVALID_ACCESS)
     */
    public List<ReadGlucoseHistoryResponse> execute(Long memberId, Long patientId) {
        validateCareGiver(memberId, patientId);
        if (glucoseHistoryCache.existsByPatientId(patientId)) {
            return glucoseHistoryCache.readAllByPatientId(patientId);
        }

        var result = glucoseHistoryRepository.findAllByPatientIdOrderByDateDesc(patientId)
                                             .stream()
                                             .map(this::convertGlucoseHistoryResponse)
                                             .toList();
        glucoseHistoryCache.createGlucoseHistories(patientId, result);
        return result;
    }

    /**
     * 혈당 기록 엔티티를 응답 DTO로 변환
     * <p>
     * 처리 단계:
     * 1. 혈당 기록 엔티티에서 ID, 날짜, 혈당 수치 추출
     * 2. 응답 DTO 생성
     * 3. 생성된 응답 DTO 반환
     *
     * @param glucoseHistory 변환할 혈당 기록 엔티티
     * @return 혈당 기록 ID, 날짜, 혈당 수치를 포함한 응답 객체
     */
    private ReadGlucoseHistoryResponse convertGlucoseHistoryResponse(GlucoseHistory glucoseHistory) {
        return ReadGlucoseHistoryResponse.of(glucoseHistory.getId(), glucoseHistory.getDate(), glucoseHistory.getSgv());
    }

    /**
     * 간병인 권한 검증 (캐시 활용)
     * <p>
     * 처리 단계:
     * 1. Redis 캐시에 회원-환자 관계가 있는지 확인
     * 2. 캐시에 있으면 즉시 반환 (권한 있음)
     * 3. 캐시에 없으면 데이터베이스에서 회원-환자 관계 존재 여부 확인
     * 4. 관계가 없으면 예외 발생
     * 5. 관계가 있으면 캐시에 저장 후 반환
     *
     * @param memberId  검증할 회원의 ID
     * @param patientId 검증할 환자의 ID
     * @throws ApplicationException 간병인 관계가 없는 경우 (INVALID_ACCESS)
     */
    private void validateCareGiver(Long memberId, Long patientId) {
        if (careGiverCache.existsByMemberIdAndPatientId(memberId, patientId)) return;
        if (!memberPatientRelationRepository.existsByMemberIdAndPatientId(memberId, patientId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }

        careGiverCache.cacheRelation(memberId, patientId);
    }
}
