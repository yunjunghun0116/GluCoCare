package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "glucose_warning_notification_history")
@Getter
public class GlucoseWarningNotificationHistory extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "glucose_history_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GlucoseHistory glucoseHistory;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "glucose_warning_type")
    private GlucoseWarningType glucoseWarningType;

    protected GlucoseWarningNotificationHistory() {
    }

    public GlucoseWarningNotificationHistory(Member member, GlucoseHistory glucoseHistory, GlucoseWarningType glucoseWarningType) {
        this.member = member;
        this.glucoseHistory = glucoseHistory;
        this.glucoseWarningType = glucoseWarningType;
    }
}
