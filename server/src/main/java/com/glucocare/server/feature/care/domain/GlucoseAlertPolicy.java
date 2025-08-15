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
    @JoinColumn(name = "care_giver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CareGiver careGiver;
    @NotNull
    @Column(name = "high_risk_value")
    private Integer highRiskValue;
    @NotNull
    @Column(name = "very_high_risk_value")
    private Integer veryHighRiskValue;


    protected GlucoseAlertPolicy() {
    }

    public GlucoseAlertPolicy(CareGiver careGiver) {
        this.careGiver = careGiver;
        this.highRiskValue = 150;
        this.veryHighRiskValue = 200;
    }

    public void updateHighRiskValue(Integer newHighRiskValue) {
        this.highRiskValue = newHighRiskValue;
    }


    public void updateVeryHighRiskValue(Integer newVeryHighRiskValue) {
        this.veryHighRiskValue = newVeryHighRiskValue;
    }
}
