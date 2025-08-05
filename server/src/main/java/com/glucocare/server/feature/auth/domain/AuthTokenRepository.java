package com.glucocare.server.feature.auth.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByRefreshToken(String refreshToken);

    Optional<AuthToken> findByMember(Member member);

    Boolean existsByMember(Member member);
}
