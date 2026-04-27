package com.wallet.api.wallet.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto {

    private Long id;
    private String walletName;
    private Long balance;
    private String colorCode;
    private String status;
    private LocalDateTime createdAt;
}
