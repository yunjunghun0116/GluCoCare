package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.RefreshTokenRequest;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 리프레시 토큰 갱신 기능을 담당하는 Use Case 클래스
 * 
 * 이 클래스는 만료된 액세스 토큰을 갱신하기 위한 비즈니스 로직을 처리합니다.
 * 리프레시 토큰을 검증하고 새로운 JWT 토큰 쌍을 발급합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenUseCase {
    private final JwtProvider jwtProvider;
    private final AuthTokenRepository authTokenRepository;

    /**
     * 리프레시 토큰을 사용하여 새로운 JWT 토큰을 발급하는 메인 메서드
     * 
     * 이 메서드는 다음과 같은 과정을 수행합니다:
     * 1. 요청된 리프레시 토큰으로 인증 토큰 조회
     * 2. 인증 토큰이 존재하지 않으면 EXPIRED_TOKEN 예외 발생
     * 3. 인증 토큰에 연결된 회원으로 새로운 JWT 토큰 쌍 생성
     * 4. 기존 리프레시 토큰을 새로운 토큰으로 업데이트
     * 5. 새로운 JWT 토큰 쌍 반환
     * 
     * @param request 리프레시 토큰을 포함한 토큰 갱신 요청 객체
     * @return 새로 발급된 액세스와 리프레시 토큰을 포함한 인증 응답 객체
     * @throws ApplicationException 리프레시 토큰이 만료되었거나 유효하지 않은 경우
     */
    public AuthResponse execute(RefreshTokenRequest request) {
        var authToken = authTokenRepository.findByRefreshToken(request.token())
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.EXPIRED_TOKEN));
        var generatedToken = jwtProvider.generateToken(authToken.getMember());
        authToken.updateRefreshToken(generatedToken.refreshToken());
        authTokenRepository.save(authToken);
        return generatedToken;
    }


}
