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

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenUseCase {
    private final JwtProvider jwtProvider;
    private final AuthTokenRepository authTokenRepository;

    public AuthResponse execute(RefreshTokenRequest request) {
        var authToken = authTokenRepository.findByRefreshToken(request.token())
                                           .orElseThrow(() -> new ApplicationException(ErrorMessage.EXPIRED_TOKEN));
        var generatedToken = jwtProvider.generateToken(authToken.getMember());
        authToken.updateRefreshToken(generatedToken.refreshToken());
        authTokenRepository.save(authToken);
        return generatedToken;
    }


}
