package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthToken;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.LoginRequest;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원 로그인 Use Case
 * <p>
 * 이메일과 비밀번호 기반의 회원 로그인을 처리하고, JWT 토큰 쌍(Access Token, Refresh Token)을 발급합니다.
 * 로그인 성공 시 Refresh Token은 데이터베이스에 저장되어 향후 토큰 갱신에 사용됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoginUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;

    /**
     * 회원 로그인 처리
     * <p>
     * 비즈니스 로직 순서:
     * 1. 이메일로 회원 조회
     * 2. BCrypt 알고리즘으로 비밀번호 검증
     * 3. JWT 토큰 쌍(Access Token, Refresh Token) 생성
     * 4. Refresh Token을 데이터베이스에 저장 (신규 생성 또는 업데이트)
     * 5. 생성된 토큰 쌍 반환
     *
     * @param request 이메일과 비밀번호를 포함한 로그인 요청
     * @return Access Token과 Refresh Token을 포함한 인증 응답
     * @throws ApplicationException 이메일이 존재하지 않거나 비밀번호가 일치하지 않는 경우 (INVALID_LOGIN_REQUEST_MATCHES)
     */
    public AuthResponse execute(LoginRequest request) {
        var member = readMemberWithLoginRequest(request);
        return saveRefreshToken(member);
    }

    /**
     * 로그인 자격 증명 검증 및 회원 조회
     * <p>
     * 처리 단계:
     * 1. 요청된 이메일로 데이터베이스에서 회원 엔티티 조회
     * 2. 회원이 존재하지 않으면 예외 발생
     * 3. BCrypt PasswordEncoder로 평문 비밀번호와 해시된 비밀번호 비교
     * 4. 비밀번호가 일치하지 않으면 예외 발생
     * 5. 검증 성공 시 회원 엔티티 반환
     *
     * @param request 이메일과 비밀번호를 포함한 로그인 요청
     * @return 검증된 회원 엔티티
     * @throws ApplicationException 이메일이 없거나 비밀번호가 틀린 경우 (INVALID_LOGIN_REQUEST_MATCHES)
     */
    private Member readMemberWithLoginRequest(LoginRequest request) {
        var member = memberRepository.findByEmail(request.email())
                                     .orElseThrow(() -> new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES));
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ApplicationException(ErrorMessage.INVALID_LOGIN_REQUEST_MATCHES);
        }
        return member;
    }

    /**
     * Refresh Token 저장 또는 업데이트
     * <p>
     * 처리 단계:
     * 1. 회원 정보로 JWT 토큰 쌍(Access Token, Refresh Token) 생성
     * 2. 데이터베이스에서 해당 회원의 기존 Refresh Token 조회
     * 3-A. 기존 토큰이 없으면: 새로운 AuthToken 엔티티 생성 및 저장
     * 3-B. 기존 토큰이 있으면: 기존 엔티티의 Refresh Token만 업데이트 (JPA Dirty Checking으로 자동 저장)
     * 4. 생성된 JWT 토큰 쌍 반환
     *
     * @param member 로그인한 회원 엔티티
     * @return Access Token과 Refresh Token을 포함한 인증 응답
     */
    private AuthResponse saveRefreshToken(Member member) {
        var tokenResponse = jwtProvider.generateToken(member);
        var authToken = authTokenRepository.findByMember(member);
        if (authToken.isEmpty()) {
            var newToken = new AuthToken(member, tokenResponse.refreshToken());
            authTokenRepository.save(newToken);
            return tokenResponse;
        }
        var savedAuthToken = authToken.get();
        savedAuthToken.updateRefreshToken(tokenResponse.refreshToken());
        return tokenResponse;
    }
}
