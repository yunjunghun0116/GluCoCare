package com.glucocare.server.feature.care.domain;

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
@Table(name = "glucose_alert_policy")
@Getter
public class GlucoseAlertPolicy extends BaseEntity {
    @NotNull
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "care_relation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CareRelation careRelation;
    @NotNull
    @Column(name = "high_risk_value")
    private Integer highRiskValue;
    @NotNull
    @Column(name = "very_high_risk_value")
    private Integer veryHighRiskValue;


    protected GlucoseAlertPolicy() {
    }

    public GlucoseAlertPolicy(CareRelation careRelation) {
        this.careRelation = careRelation;
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
