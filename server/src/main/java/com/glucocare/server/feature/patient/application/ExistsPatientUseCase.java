package com.glucocare.server.feature.patient.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import com.glucocare.server.feature.care.domain.RelationType;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 환자 등록 여부 확인 Use Case
 * <p>
 * 특정 회원이 환자로 등록되어 있는지 확인합니다.
 * 한 회원은 하나의 환자 관계(PATIENT 타입)만 가질 수 있습니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExistsPatientUseCase {
    private final MemberRepository memberRepository;
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    /**
     * 환자 등록 여부 확인
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 해당 회원의 환자 관계(PATIENT 타입) 존재 여부 확인
     * 3. 존재하면 true (환자 등록됨), 존재하지 않으면 false (환자 미등록) 반환
     *
     * @param memberId 확인할 회원의 ID
     * @return true: 환자로 등록되어 있음, false: 환자로 등록되지 않음
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public Boolean execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        return memberPatientRelationRepository.existsByMemberAndRelationType(member, RelationType.PATIENT);
    }
}
