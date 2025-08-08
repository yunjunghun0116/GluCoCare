package com.glucocare.server.feature.caregiver.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.caregiver.domain.CareGiver;
import com.glucocare.server.feature.caregiver.domain.CareGiverRepository;
import com.glucocare.server.feature.caregiver.dto.ReadCareGiverResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인(CareGiver) 관계 단건 조회를 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 간병인 관계 ID를 통해 특정 간병인 관계를 조회하는 비즈니스 로직을 처리합니다.
 * 조회된 간병인 관계 정보는 응답 객체로 변환되어 반환됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadCareGiverUseCase {
    private final CareGiverRepository careGiverRepository;

    /**
     * 간병인 관계 ID로 특정 간병인 관계를 조회하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 간병인 관계 ID로 데이터베이스에서 간병인 관계 조회
     * 2. 간병인 관계가 존재하지 않으면 NOT_FOUND 예외 발생
     * 3. 조회된 간병인 관계를 응답 객체로 변환하여 반환
     *
     * @param id 조회할 간병인 관계의 ID
     * @return 간병인 관계 정보를 포함한 조회 응답 객체
     * @throws ApplicationException 간병인 관계를 찾을 수 없는 경우
     */
    public ReadCareGiverResponse execute(Long id) {
        var careGiver = careGiverRepository.findById(id)
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return convertCareGiverResponse(careGiver);
    }

    /**
     * 간병인 관계 엔티티를 조회용 응답 객체로 변환하는 메서드
     * <p>
     * 간병인 관계 엔티티에서 필요한 정보(간병인 ID, 환자 정보)를 추출하여
     * 클라이언트에게 반환할 조회 전용 응답 객체를 생성합니다.
     *
     * @param careGiver 변환할 간병인 관계 엔티티
     * @return 간병인 ID, 환자 ID, 환자 이름을 포함한 조회 응답 객체
     */
    private ReadCareGiverResponse convertCareGiverResponse(CareGiver careGiver) {
        var patient = careGiver.getPatient();
        return ReadCareGiverResponse.of(careGiver.getId(), patient.getId(), patient.getName());
    }
}
