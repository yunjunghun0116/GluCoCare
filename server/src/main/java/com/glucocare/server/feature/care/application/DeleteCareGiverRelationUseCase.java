package com.glucocare.server.feature.care.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.care.domain.MemberPatientRelationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 간병인 관계 삭제 Use Case
 * <p>
 * 회원과 환자 간의 간병인 관계를 삭제합니다. 권한 검증을 통해 본인의 간병인 관계만 삭제할 수 있도록 보장합니다.
 * 관계 삭제 시 연관된 혈당 알림 정책도 CASCADE 옵션에 의해 함께 삭제됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCareGiverRelationUseCase {
    private final MemberPatientRelationRepository memberPatientRelationRepository;

    /**
     * 간병인 관계 삭제 (권한 검증 포함)
     * <p>
     * 비즈니스 로직 순서:
     * 1. 간병인 관계 ID로 데이터베이스에서 간병인 관계 엔티티 조회
     * 2. 조회된 간병인 관계의 소유자(Member)와 요청한 회원 ID가 일치하는지 권한 검증
     * 3. 권한이 없으면 예외 발생 (다른 회원의 간병인 관계 삭제 시도 차단)
     * 4. 권한이 있으면 간병인 관계를 데이터베이스에서 삭제
     * 5. CASCADE 설정에 의해 연관된 GlucoseAlertPolicy도 자동 삭제
     *
     * @param memberId 삭제를 요청한 회원의 ID (권한 검증용)
     * @param id 삭제할 간병인 관계의 ID
     * @throws ApplicationException 간병인 관계를 찾을 수 없는 경우 (NOT_FOUND), 권한이 없는 경우 (INVALID_ACCESS)
     */
    public void execute(Long memberId, Long id) {
        var careGiverRelation = memberPatientRelationRepository.findById(id)
                                                               .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        if (!careGiverRelation.getMember()
                              .getId()
                              .equals(memberId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
        memberPatientRelationRepository.delete(careGiverRelation);
    }

}
