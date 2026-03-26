package com.glucocare.server.feature.mission.domain;

import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "mission")
@Getter
public class Mission extends BaseEntity {
    @NotNull
    @Column(name = "title")
    private String title;
    @NotNull
    @Column(name = "description")
    private String description;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "mission_type")
    private MissionType missionType;
    @NotNull
    @Column(name = "threshold")
    private Double threshold;
    @NotNull
    @Column(name = "reward_point")
    private Long rewardPoint;
    @NotNull
    @Column(name = "is_active")
    private Boolean isActive = true;


    protected Mission() {
    }

    public Mission(String title, String description, MissionType missionType, Double threshold, Long rewardPoint) {
        this.title = title;
        this.description = description;
        this.missionType = missionType;
        this.threshold = threshold;
        this.rewardPoint = rewardPoint;
    }

    public void update(String title, String description, Double threshold, Long rewardPoint, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.threshold = threshold;
        this.rewardPoint = rewardPoint;
        this.isActive = isActive;
    }
}
