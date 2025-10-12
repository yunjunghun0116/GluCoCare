package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelation;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.dto.ReadCareGiverRelationResponse;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 간병인 관계 전체 조회 Use Case
 * <p>
 * 특정 회원에게 연결된 모든 간병인 관계를 조회합니다.
 * 조회된 관계 목록은 환자 정보를 포함한 응답 DTO 리스트로 변환되어 반환됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReadAllCareGiverRelationUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    /**
     * 간병인 관계 목록 조회
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 해당 회원과 연결된 모든 간병인 관계 조회
     * 3. 조회된 간병인 관계들을 응답 DTO 리스트로 변환하여 반환
     *
     * @param memberId 조회를 요청한 회원의 ID
     * @return 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답 리스트
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public List<ReadCareGiverRelationResponse> execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        var careGiverRelations = memberPatientRelationRepository.findAllByMember(member);
        return careGiverRelations.stream()
                                 .map((this::convertCareGiverRelationResponse))
                                 .toList();
    }

    /**
     * 간병인 관계 엔티티를 응답 DTO로 변환
     * <p>
     * 처리 단계:
     * 1. 간병인 관계 엔티티에서 환자 엔티티 추출
     * 2. 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답 DTO 생성
     * 3. 생성된 응답 DTO 반환
     *
     * @param memberPatientRelation 변환할 간병인 관계 엔티티
     * @return 간병인 관계 ID, 환자 ID, 환자 이름을 포함한 응답 객체
     */
    private ReadCareGiverRelationResponse convertCareGiverRelationResponse(MemberPatientRelation memberPatientRelation) {
        var patient = memberPatientRelation.getPatient();
        return ReadCareGiverRelationResponse.of(memberPatientRelation.getId(), patient.getId(), patient.getName());
    }
}
