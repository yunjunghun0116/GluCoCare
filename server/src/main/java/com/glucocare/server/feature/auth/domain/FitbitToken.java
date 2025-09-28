package com.glucocare.server.feature.auth.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "fitbit_token")
@Getter
public class FitbitToken extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @Column(name = "fitbit_user_id")
    private String fitbitUserId;
    @NotNull
    @Column(name = "access_token")
    private String accessToken;
    @NotNull
    @Column(name = "expires_in")
    private Integer expiresIn;
    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;

    protected FitbitToken() {
    }

    public FitbitToken(Member member, String fitbitUserId, String accessToken, Integer expiresIn, String refreshToken) {
        this.member = member;
        this.fitbitUserId = fitbitUserId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }

    public void refreshToken(String fitbitUserId, String accessToken, Integer expiresIn, String refreshToken) {
        this.fitbitUserId = fitbitUserId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }
}
