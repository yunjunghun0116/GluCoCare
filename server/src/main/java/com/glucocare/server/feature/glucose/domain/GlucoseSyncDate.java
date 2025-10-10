package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.patient.domain.Patient;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "glucose_sync_date")
@Getter
public class GlucoseSyncDate extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;
    @NotNull
    @Column(name = "date")
    private LocalDate date;

    protected GlucoseSyncDate() {
    }

    public GlucoseSyncDate(Patient patient, LocalDate date) {
        this.patient = patient;
        this.date = date;
    }
}
