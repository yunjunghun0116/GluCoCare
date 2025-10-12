package com.glucocare.server.feature.auth.application;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.auth.domain.AuthTokenRepository;
import com.glucocare.server.feature.auth.dto.AuthResponse;
import com.glucocare.server.feature.auth.dto.RefreshTokenRequest;
import com.glucocare.server.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JWT 토큰 갱신 Use Case
 * <p>
 * 만료된 Access Token을 Refresh Token으로 갱신하는 기능을 제공합니다.
 * Refresh Token의 유효성을 검증하고, 새로운 JWT 토큰 쌍을 발급하여 지속적인 인증 상태를 유지합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenUseCase {
    private final JwtProvider jwtProvider;
    private final AuthTokenRepository authTokenRepository;

    /**
     * Refresh Token으로 JWT 토큰 갱신
     * <p>
     * 비즈니스 로직 순서:
     * 1. 요청된 Refresh Token으로 데이터베이스에서 AuthToken 엔티티 조회
     * 2. 조회 실패 시 예외 발생 (만료되었거나 유효하지 않은 토큰)
     * 3. AuthToken과 연결된 회원 정보로 새로운 JWT 토큰 쌍 생성
     * 4. 데이터베이스의 Refresh Token을 새로운 토큰으로 업데이트 (JPA Dirty Checking)
     * 5. 새로 생성된 JWT 토큰 쌍 반환
     *
     * @param request Refresh Token을 포함한 토큰 갱신 요청
     * @return 새로 발급된 Access Token과 Refresh Token을 포함한 인증 응답
     * @throws ApplicationException Refresh Token이 데이터베이스에 없거나 만료된 경우 (EXPIRED_TOKEN)
     */
    public AuthResponse execute(RefreshTokenRequest request) {
        var authToken = authTokenRepository.findByRefreshToken(request.token())
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.EXPIRED_TOKEN));
        var generatedToken = jwtProvider.generateToken(authToken.getMember());
        authToken.updateRefreshToken(generatedToken.refreshToken());
        return generatedToken;
    }
}
