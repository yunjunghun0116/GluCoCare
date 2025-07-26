package com.glucocare.server.feature.patient.domain;

import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "patient")
@Getter
public class Patient extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(
            name = "cgm_server_url",
            unique = true
    )
    private String cgmServerUrl;

    protected Patient() {
    }

    public Patient(String name, String cgmServerUrl) {
        this.name = name;
        this.cgmServerUrl = cgmServerUrl;
    }

    public void updateCgmServerUrl(String newCgmServerUrl) {
        this.cgmServerUrl = newCgmServerUrl;
    }
}
