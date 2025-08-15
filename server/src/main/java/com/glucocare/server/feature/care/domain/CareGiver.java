package com.glucocare.server.feature.care.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.feature.patient.domain.Patient;
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
@Table(name = "care_giver")
@Getter
public class CareGiver extends BaseEntity {
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

    protected CareGiver() {
    }

    public CareGiver(Member member, Patient patient) {
        this.member = member;
        this.patient = patient;
    }
}
