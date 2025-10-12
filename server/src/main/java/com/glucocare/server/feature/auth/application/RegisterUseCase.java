package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthToken;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.RegisterRequest;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.member.domain.MemberRepository;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원가입 Use Case
 * <p>
 * 신규 회원 등록을 처리하며, 이메일 중복 검증, 비밀번호 암호화, JWT 토큰 발급까지 전체 회원가입 프로세스를 수행합니다.
 * 회원가입 성공 시 즉시 로그인 상태가 되도록 JWT 토큰을 반환합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;

    /**
     * 회원가입 처리
     * <p>
     * 비즈니스 로직 순서:
     * 1. 이메일 중복 검증
     * 2. 비밀번호 BCrypt 암호화
     * 3. 회원 엔티티 생성 및 데이터베이스 저장
     * 4. JWT 토큰 쌍(Access Token, Refresh Token) 생성
     * 5. Refresh Token을 데이터베이스에 저장
     * 6. 생성된 토큰 쌍 반환 (즉시 로그인 상태)
     *
     * @param request 이름, 이메일, 비밀번호를 포함한 회원가입 요청
     * @return Access Token과 Refresh Token을 포함한 인증 응답
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우 (DUPLICATED_EMAIL)
     */
    public AuthResponse execute(RegisterRequest request) {
        var member = saveMemberWithRequest(request);
        return saveRefreshToken(member);
    }

    /**
     * 이메일 중복 검증
     * <p>
     * 처리 단계:
     * 1. 데이터베이스에서 동일한 이메일을 가진 회원 존재 여부 확인
     * 2. 이미 등록된 이메일이면 예외 발생
     * 3. 사용 가능한 이메일이면 정상 처리
     *
     * @param email 중복 검증할 이메일 주소
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우 (DUPLICATED_EMAIL)
     */
    private void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorMessage.DUPLICATED_EMAIL);
        }
    }

    /**
     * 회원 엔티티 생성 및 저장
     * <p>
     * 처리 단계:
     * 1. 이메일 중복 검증 수행
     * 2. BCrypt 알고리즘으로 평문 비밀번호를 해시 암호화
     * 3. 이름, 이메일, 암호화된 비밀번호로 Member 엔티티 생성
     * 4. 생성된 회원 엔티티를 데이터베이스에 저장
     * 5. 저장된 회원 엔티티 반환
     *
     * @param request 이름, 이메일, 비밀번호를 포함한 회원가입 요청
     * @return 데이터베이스에 저장된 회원 엔티티
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우 (DUPLICATED_EMAIL)
     */
    private Member saveMemberWithRequest(RegisterRequest request) {
        validateEmail(request.email());
        var encodedPassword = passwordEncoder.encode(request.password());
        var member = new Member(request.name(), request.email(), encodedPassword);
        return memberRepository.save(member);
    }

    /**
     * Refresh Token 생성 및 저장
     * <p>
     * 처리 단계:
     * 1. 신규 회원 정보로 JWT 토큰 쌍(Access Token, Refresh Token) 생성
     * 2. 새로운 AuthToken 엔티티 생성 (회원 정보와 Refresh Token 포함)
     * 3. AuthToken 엔티티를 데이터베이스에 저장
     * 4. 생성된 JWT 토큰 쌍 반환
     *
     * @param member 신규 가입한 회원 엔티티
     * @return Access Token과 Refresh Token을 포함한 인증 응답
     */
    private AuthResponse saveRefreshToken(Member member) {
        var tokenResponse = jwtProvider.generateToken(member);
        var authToken = new AuthToken(member, tokenResponse.refreshToken());
        authTokenRepository.save(authToken);
        return tokenResponse;
    }
}
