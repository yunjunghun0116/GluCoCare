package com.glucocare.server.feature.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Email(message = "허용되지 않은 형식의 이메일입니다.") String email,
        @Size(
                min = 8,
                max = 20,
                message = "패스워드의 길이는 8자 이상, 20자 이내이어야 합니다."
        ) String password
) {
}
