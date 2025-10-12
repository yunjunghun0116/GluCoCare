package com.glucocare.server.feature.care.domain;

import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "glucose_alert_policy")
@Getter
public class GlucoseAlertPolicy extends BaseEntity {
    @NotNull
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_patient_relation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MemberPatientRelation memberPatientRelation;
    @NotNull
    @Column(name = "high_risk_value")
    private Integer highRiskValue;
    @NotNull
    @Column(name = "very_high_risk_value")
    private Integer veryHighRiskValue;


    protected GlucoseAlertPolicy() {
    }

    public GlucoseAlertPolicy(MemberPatientRelation memberPatientRelation) {
        this.memberPatientRelation = memberPatientRelation;
        this.highRiskValue = 140;
        this.veryHighRiskValue = 180;
    }

    public void updateHighRiskValue(Integer newHighRiskValue) {
        this.highRiskValue = newHighRiskValue;
    }


    public void updateVeryHighRiskValue(Integer newVeryHighRiskValue) {
        this.veryHighRiskValue = newVeryHighRiskValue;
    }
}
