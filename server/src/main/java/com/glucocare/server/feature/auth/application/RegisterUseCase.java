package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
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
 * 회원 회원가입 기능을 담당하는 Use Case 클래스
 * <p>
 * 이 클래스는 새로운 회원의 가입 요청을 처리하고 JWT 토큰을 발급하는 비즈니스 로직을 처리합니다.
 * 이메일 중복 검사와 비밀번호 암호화를 포함한 전체 가입 프로세스를 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUseCase {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입을 처리하는 메인 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 가입 요청 정보로 새로운 회원 생성 및 저장
     * 2. 생성된 회원으로 JWT 토큰 생성
     * 3. 인증 응답 객체로 반환
     *
     * @param request 회원가입 요청 정보 (이름, 이메일, 비밀번호 포함)
     * @return JWT 토큰을 포함한 인증 응답 객체
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우
     */
    public AuthResponse execute(RegisterRequest request) {
        var member = saveMemberWithRequest(request);
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    /**
     * 이메일 중복 여부를 검증하는 메서드
     * <p>
     * 데이터베이스에 동일한 이메일이 이미 등록되어 있는지 확인합니다.
     * 중복된 이메일이 등록되어 있는 경우 예외를 발생시킵니다.
     *
     * @param email 검증할 이메일 주소
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우
     */
    private void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorMessage.DUPLICATED_EMAIL);
        }
    }

    /**
     * 가입 요청 정보로 새로운 회원을 생성하고 저장하는 메서드
     * <p>
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 이메일 중복 검사
     * 2. 비밀번호 암호화 (BCrypt 알고리즘 사용)
     * 3. 새로운 회원 엔티티 생성 및 데이터베이스 저장
     *
     * @param request 회원가입 요청 정보
     * @return 생성되고 저장된 회원 엔티티
     * @throws ApplicationException 이메일이 이미 등록되어 있는 경우
     */
    private Member saveMemberWithRequest(RegisterRequest request) {
        validateEmail(request.email());
        var encodedPassword = passwordEncoder.encode(request.password());
        var member = new Member(request.name(), request.email(), encodedPassword);
        return memberRepository.save(member);
    }
}
