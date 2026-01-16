package com.glucocare.server.feature.member.domain;

import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "member")
@Getter
public class Member extends BaseEntity {
    @NotNull
    @Column(
            name = "email",
            unique = true
    )
    private String email;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "is_patient")
    private Boolean isPatient = false;
    @NotNull
    @Column(name = "access_code")
    private String accessCode = "";
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole = MemberRole.MEMBER;

    protected Member() {
    }

    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateMemberToPatient() {
        this.isPatient = true;
    }

    public void updateAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public void validateAccessCode(String accessCode) {
        if (!this.accessCode.equals(accessCode)) {
            throw new ApplicationException(ErrorMessage.BAD_REQUEST);
        }
    }
}
