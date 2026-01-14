package com.glucocare.server.feature.care.domain;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "member_patient_relation")
@Getter
public class CareRelation extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member patient;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType = RelationType.CAREGIVER;
    @OneToOne(
            mappedBy = "memberPatientRelation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private GlucoseAlertPolicy glucoseAlertPolicy;


    protected CareRelation() {
    }

    public CareRelation(Member member, Member patient, RelationType relationType) {
        this.member = member;
        this.patient = patient;
        this.relationType = relationType;
        this.glucoseAlertPolicy = new GlucoseAlertPolicy(this);  // 자동 생성
    }

    public void validateOwnership(Long memberId) {
        if (!this.member.getId()
                        .equals(memberId)) {
            throw new ApplicationException(ErrorMessage.INVALID_ACCESS);
        }
    }
}
