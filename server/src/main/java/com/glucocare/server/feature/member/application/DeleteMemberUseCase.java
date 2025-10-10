package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 삭제 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 회원 탈퇴 또는 회원 삭제 요청을 처리하는 비즈니스 로직을 처리합니다.
 * 회원 ID를 통해 회원을 조회한 후 데이터베이스에서 삭제를 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteMemberUseCase {
    private final MemberRepository memberRepository;

    /**
     * 회원을 삭제하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 주어진 memberId로 회원 조회
     * 2. 회원이 존재하지 않으면 NOT_FOUND 예외 발생
     * 3. 조회된 회원을 데이터베이스에서 영구 삭제
     *
     * @param memberId 삭제할 회원의 ID
     * @throws ApplicationException 회원을 찾을 수 없는 경우
     */
    public void execute(Long memberId) {
        var member = memberRepository.findById(memberId)
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.NOT_FOUND));
        memberRepository.delete(member);
    }
}
