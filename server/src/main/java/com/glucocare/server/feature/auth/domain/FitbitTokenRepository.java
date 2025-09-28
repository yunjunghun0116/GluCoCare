package com.glucocare.server.feature.auth.domain;

import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FitbitTokenRepository extends JpaRepository<FitbitToken, Long> {
    Optional<FitbitToken> findByMember(Member member);

    Boolean existsByMember(Member member);
}
