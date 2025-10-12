package com.glucocare.server.feature.care.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "member_patient_relation")
@Getter
public class MemberPatientRelation extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType = RelationType.CAREGIVER;

    protected MemberPatientRelation() {
    }

    public MemberPatientRelation(Member member, Patient patient, RelationType relationType) {
        this.member = member;
        this.patient = patient;
        this.relationType = relationType;
    }
}
