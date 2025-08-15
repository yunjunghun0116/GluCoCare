package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlucoseWarningNotificationHistoryRepository extends JpaRepository<GlucoseWarningNotificationHistory, Long> {
    Boolean existsByMemberAndGlucoseHistory(Member member, GlucoseHistory glucoseHistory);
}

