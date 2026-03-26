package com.glucocare.server.feature.point.domain;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
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
@Table(name = "point_history")
@Getter
public class PointHistory extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private PointTransactionType type;
    @NotNull
    @Column(name = "amount")
    private Long amount;
    @NotNull
    @Column(name = "balance_after")
    private Long balanceAfter;
    @NotNull
    @Column(name = "total_spent")
    private String description;

    protected PointHistory() {
    }

    public PointHistory(Member member, PointTransactionType type, Long amount, Long balanceAfter, String description) {
        if (amount < 0) {
            throw new ApplicationException(ErrorMessage.AMOUNT_MUST_BE_POSITIVE);
        }
        this.member = member;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }
}
