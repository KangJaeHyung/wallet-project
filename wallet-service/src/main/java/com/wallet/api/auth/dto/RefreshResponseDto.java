package com.wallet.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshResponseDto {

    private String accessToken;
    private long accessTokenExpiresIn;
    private String tokenType;
}
