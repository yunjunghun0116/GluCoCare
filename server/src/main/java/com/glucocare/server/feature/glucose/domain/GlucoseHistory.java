package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "glucose_history")
@Getter
public class GlucoseHistory extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;
    @NotNull
    @Column(name = "sgv")
    private Integer sgv;
    @NotNull
    @Column(name = "date")
    private Long date;

    protected GlucoseHistory() {
    }

    public GlucoseHistory(Patient patient, Integer sgv, Long date) {
        this.patient = patient;
        this.sgv = sgv;
        this.date = date;
    }
}
