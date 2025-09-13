package com.glucocare.server.feature.glucose.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.CareGiverRepository;
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
 * 특정 환자의 모든 혈당 기록을 조회하는 Use Case 클래스
 * <p>
 * 이 클래스는 간병인이 담당하는 환자의 혈당 기록을 조회하는 비즈니스 로직을 처리합니다.
 * 권한 검증을 통해 해당 회원이 요청한 환자의 간병인인지 확인한 후 데이터를 제공합니다.
 * 성능 최적화를 위해 Redis 캐시를 활용하여, 캐시된 데이터가 있으면 데이터베이스 없이 바로 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllGlucoseHistoryUseCase {
    private final GlucoseHistoryRepository glucoseHistoryRepository;
    private final CareGiverRepository careGiverRepository;
    private final CareGiverCache careGiverCache;
    private final GlucoseHistoryCache glucoseHistoryCache;

    /**
     * 특정 환자의 모든 혈당 기록을 조회하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId와 patientId로 회원과 환자 조회
     * 2. 해당 회원이 요청한 환자의 간병인인지 권한 검증
     * 3. Redis 캐시에 환자 혈당 데이터가 있는지 확인
     * 4. 캐시된 데이터가 있으면 캐시에서 조회, 없으면 데이터베이스에서 조회
     * 5. 데이터베이스에서 조회한 경우 결과를 캐시에 저장
     * 6. 조회된 혈당 기록들을 응답 객체 리스트로 변환하여 반환
     *
     * @param memberId  혈당 기록을 조회하려는 회원의 ID
     * @param patientId 혈당 기록을 조회할 환자의 ID
     * @return 해당 환자의 모든 혈당 기록을 날짜 역순으로 정렬한 응답 객체 리스트
     * @throws ApplicationException 회원이나 환자를 찾을 수 없거나, 간병인 권한이 없는 경우
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
     * 혈당 기록 엔티티를 조회용 응답 객체로 변환하는 메서드
     * <p>
     * 혈당 기록 엔티티에서 필요한 정보(ID, 날짜, 혈당 수치)를 추출하여
     * 클라이언트에게 반환할 응답 객체로 변환합니다.
     *
     * @param glucoseHistory 변환할 혈당 기록 엔티티
     * @return 혈당 기록 ID, 날짜, 혈당 수치를 포함한 조회 응답 객체
     */
    private ReadGlucoseHistoryResponse convertGlucoseHistoryResponse(GlucoseHistory glucoseHistory) {
        return ReadGlucoseHistoryResponse.of(glucoseHistory.getId(), glucoseHistory.getDate(), glucoseHistory.getSgv());
    }

    private void validateCareGiver(Long memberId, Long patientId) {
        if (careGiverCache.existsByMemberIdAndPatientId(memberId, patientId)) return;
        if (!careGiverRepository.existsByMemberIdAndPatientId(memberId, patientId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }

        careGiverCache.createGlucoseHistories(memberId, patientId);
    }
}
