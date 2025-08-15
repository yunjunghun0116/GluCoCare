package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "danger_notification_history")
@Getter
public class DangerNotificationHistory extends BaseEntity {
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

    protected DangerNotificationHistory() {
    }

    public DangerNotificationHistory(Member member, GlucoseHistory glucoseHistory) {
        this.member = member;
        this.glucoseHistory = glucoseHistory;
    }
}
