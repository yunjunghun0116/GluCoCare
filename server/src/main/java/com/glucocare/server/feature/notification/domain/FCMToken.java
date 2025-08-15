package com.glucocare.server.feature.fcmtoken.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "fcm_token")
@Getter
public class FCMToken extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @Column(name = "fcm_token")
    private String fcmToken;

    protected FCMToken() {
    }

    public FCMToken(Member member, String fcmToken) {
        this.member = member;
        this.fcmToken = fcmToken;
    }

    public void updateFCMToken(String newFCMToken) {
        this.fcmToken = newFCMToken;
    }
}
