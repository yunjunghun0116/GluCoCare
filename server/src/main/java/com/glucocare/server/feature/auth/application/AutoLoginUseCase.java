package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 자동 로그인 기능을 담당하는 Use Case 클래스
 * 
 * 이 클래스는 JWT 토큰을 통한 자동 로그인 요청을 검증하는 비즈니스 로직을 처리합니다.
 * 토큰에서 추출된 회원 ID가 실제로 존재하는 회원인지 확인합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AutoLoginUseCase {
    private final MemberRepository memberRepository;

    /**
     * 자동 로그인을 위한 회원 존재 여부를 검증하는 메인 메서드
     * 
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. JWT 토큰에서 추출된 회원 ID로 회원 존재 여부 확인
     * 2. 회원이 존재하지 않으면 BAD_REQUEST 예외 발생
     * 3. 회원이 존재하면 자동 로그인 허용
     * 
     * @param memberId JWT 토큰에서 추출된 회원 ID
     * @throws ApplicationException 회원이 존재하지 않는 경우
     */
    public void execute(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
    }
}
