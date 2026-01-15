package com.glucocare.server.feature.notification.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "fcm_token")
@Getter
public class FcmToken extends BaseEntity {
    @NotNull
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @Column(name = "fcm_token")
    private String fcmToken;

    protected FcmToken() {
    }

    public FcmToken(Member member, String fcmToken) {
        this.member = member;
        this.fcmToken = fcmToken;
    }

    public void updateFcmToken(String newFcmToken) {
        this.fcmToken = newFcmToken;
    }
}
