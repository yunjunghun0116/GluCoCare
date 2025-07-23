package com.glucocare.server.feature.member.domain;

import com.glucocare.server.shared.domain.BaseEntity;
import jakarta.persistence.*;
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
}
