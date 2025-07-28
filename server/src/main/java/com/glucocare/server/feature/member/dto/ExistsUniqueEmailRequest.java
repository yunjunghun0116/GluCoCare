package com.glucocare.server.feature.member.dto;

import jakarta.validation.constraints.Email;

public record ValidUniqueEmailRequest(
        @Email(message = "허용되지 않은 형식의 이메일입니다.") String email
) {
}
