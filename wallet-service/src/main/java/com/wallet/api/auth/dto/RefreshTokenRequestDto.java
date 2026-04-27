package com.wallet.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    @NotBlank(message = "클라이언트 타입은 필수입니다.")
    @Pattern(regexp = "^(WEB|AOS|IOS)$", message = "클라이언트 타입은 WEB, AOS, IOS 중 하나여야 합니다.")
    private String clientType;

    @NotBlank(message = "디바이스 ID는 필수입니다.")
    private String deviceId;
}
