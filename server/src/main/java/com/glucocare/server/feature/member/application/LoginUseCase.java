package com.glucocare.server.feature.member.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.feature.member.dto.AuthResponse;
import com.glucocare.server.feature.member.dto.LoginRequest;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원 로그인 기능을 담당하는 Use Case 클래스
 * 
 * 이 클래스는 회원의 로그인 요청을 처리하고 JWT 토큰을 발급하는 비즈니스 로직을 처리합니다.
 * 이메일과 비밀번호를 검증한 후 인증 토큰을 생성하여 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoginUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 회원 로그인을 처리하는 메인 메서드
     * 
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 로그인 요청 정보로 회원 검증
     * 2. 검증이 성공하면 JWT 토큰 생성
     * 3. 인증 응답 객체로 반환
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호 포함)
     * @return JWT 토큰을 포함한 인증 응답 객체
     * @throws ApplicationException 이메일이 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    public AuthResponse execute(LoginRequest request) {
        var member = readMemberWithLoginRequest(request);
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    /**
     * 로그인 요청 정보로 회원을 검증하고 조회하는 메서드
     * 
     * 이 메서드는 다음과 같은 검증 과정을 수행합니다:
     * 1. 요청된 이메일로 등록된 회원 조회
     * 2. 인코딩된 비밀번호와 입력된 비밀번호 비교 검증
     * 3. 모든 검증이 통과하면 회원 엔티티 반환
     * 
     * @param request 로그인 요청 정보
     * @return 검증된 회원 엔티티
     * @throws ApplicationException 이메일이 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    private Member readMemberWithLoginRequest(LoginRequest request) {
        var member = memberRepository.findByEmail(request.email())
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES);
        }
        return member;
    }
}
