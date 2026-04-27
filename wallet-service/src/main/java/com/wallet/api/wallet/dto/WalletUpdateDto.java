package com.wallet.api.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WalletUpdateDto {
    @NotBlank(message = "지갑 이름은 필수입니다.")
    private String walletName;
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$", message = "색상 코드는 HEX 형식이어야 합니다.")
    private String colorCode;
}
