package com.glucocare.server.feature.member.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMemberNameRequest(
        @NotBlank(message = "이름의 길이는 최소 1자 이상이어야 합니다.") String name
) {
}
