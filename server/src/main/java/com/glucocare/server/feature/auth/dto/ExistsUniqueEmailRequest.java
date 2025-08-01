package com.glucocare.server.feature.auth.dto;

import jakarta.validation.constraints.Email;

public record ExistsUniqueEmailRequest(
        @Email(message = "허용되지 않은 형식의 이메일입니다.") String email
) {
}
