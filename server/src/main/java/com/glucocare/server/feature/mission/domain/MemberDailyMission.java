package com.glucocare.server.feature.mission.domain;

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

import java.time.LocalDate;

@Entity
@Table(
        name = "member_daily_mission",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_daily_mission",
                columnNames = {"member_id", "mission_id", "mission_date"}
        )
)
@Getter
public class MemberDailyMission extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @NotNull
    @Column(name = "mission_date")
    private LocalDate missionDate;

    @NotNull
    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @NotNull
    @Column(name = "is_failed")
    private Boolean isFailed = false; // 저혈당 발생 혹은 고혈당 발생 시 실패를 기록하기 위함

    protected MemberDailyMission() {
    }

    public MemberDailyMission(Member member, Mission mission, LocalDate missionDate) {
        this.member = member;
        this.mission = mission;
        this.missionDate = missionDate;
    }

    public void complete() {
        if (isFailed) return;
        if (isCompleted) return;
        this.isCompleted = true;
    }

    public void fail() {
        this.isFailed = true;
        this.isCompleted = false;
    }
}
