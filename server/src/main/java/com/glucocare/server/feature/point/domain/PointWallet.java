package com.glucocare.server.feature.point.domain;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
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
@Table(name = "point_wallet")
@Getter
public class PointWallet extends BaseEntity {
    @NotNull
    @OneToOne(fetch = LAZY)
    @JoinColumn(
            name = "member_id",
            unique = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @Column(name = "balance")
    private Long balance;
    @NotNull
    @Column(name = "total_earned")
    private Long totalEarned;
    @NotNull
    @Column(name = "total_spent")
    private Long totalSpent;

    protected PointWallet() {
    }

    public PointWallet(Member member) {
        this.member = member;
        this.balance = 0L;
        this.totalEarned = 0L;
        this.totalSpent = 0L;
    }

    public void earn(Long amount) {
        if (amount <= 0) return;
        this.balance += amount;
        this.totalEarned += amount;
    }

    public void spend(Long amount) {
        if (amount <= 0) return;
        if (this.balance < amount) {
            throw new ApplicationException(ErrorMessage.INVALID_BALANCE_IN_WALLET);
        }
        this.balance -= amount;
        this.totalSpent += amount;
    }

    public Boolean canSpend(Long amount) {
        return amount >= 0 && this.balance >= amount;
    }
}
