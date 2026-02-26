package com.glucocare.server.feature.glucose.domain;

import com.glucocare.server.feature.member.domain.Member;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "glucose_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_glucose_history_patient_datetime",
                columnNames = {"patient_id", "dateTime"}
        )
)

@Getter
public class GlucoseHistory extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "patient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member patient;
    @NotNull
    @Column(name = "sgv")
    private Integer sgv;
    @NotNull
    @Column(name = "dateTime")
    private Long dateTime;

    protected GlucoseHistory() {
    }

    public GlucoseHistory(Member patient, Integer sgv, Long dateTime) {
        this.patient = patient;
        this.sgv = sgv;
        this.dateTime = dateTime;
    }
}
