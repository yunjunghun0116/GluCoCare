package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 자동 로그인 검증 Use Case
 * <p>
 * JWT Access Token을 통한 자동 로그인 시도 시, 토큰에 포함된 회원 ID가 유효한지 검증합니다.
 * 회원 탈퇴 등으로 인해 더 이상 존재하지 않는 회원의 토큰 사용을 방지합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AutoLoginUseCase {
    private final MemberRepository memberRepository;

    /**
     * 자동 로그인 회원 유효성 검증
     * <p>
     * 비즈니스 로직 순서:
     * 1. JWT 토큰에서 추출된 회원 ID로 데이터베이스에서 회원 존재 여부 확인
     * 2. 회원이 존재하지 않으면 예외 발생 (탈퇴 회원 또는 잘못된 토큰)
     * 3. 회원이 존재하면 자동 로그인 허용
     *
     * @param memberId JWT Access Token에서 추출된 회원 ID
     * @throws ApplicationException 회원이 데이터베이스에 존재하지 않는 경우 (BAD_REQUEST)
     */
    public void execute(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
    }
}
