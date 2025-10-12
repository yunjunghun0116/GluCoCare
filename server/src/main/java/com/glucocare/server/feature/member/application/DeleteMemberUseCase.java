package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 삭제 Use Case
 * <p>
 * 회원을 삭제합니다.
 * 회원 탈퇴 시 사용되며, 관련된 모든 데이터는 CASCADE 옵션에 의해 함께 삭제됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteMemberUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원 삭제
     * <p>
     * 비즈니스 로직 순서:
     * 1. 회원 ID로 데이터베이스에서 회원 엔티티 조회
     * 2. 회원을 데이터베이스에서 삭제
     * 3. CASCADE 설정에 의해 연관된 모든 데이터 자동 삭제 (회원-환자 관계, Refresh Token 등)
     *
     * @param memberId 삭제할 회원의 ID
     * @throws ApplicationException 회원을 찾을 수 없는 경우 (NOT_FOUND)
     */
    public void execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        memberRepository.delete(member);
    }
}
