package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DangerNotificationHistoryRepository extends JpaRepository<GlucoseWarningNotificationHistory, Long> {
    Optional<GlucoseWarningNotificationHistory> findByMemberAndGlucoseHistory(Member member, GlucoseHistory glucoseHistory);

    Boolean existsByMemberAndGlucoseHistory(Member member, GlucoseHistory glucoseHistory);
}

