package com.wallet.api.wallet;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.api.wallet.dto.WalletCreateDto;
import com.wallet.api.wallet.dto.WalletResponseDto;
import com.wallet.api.wallet.dto.WalletUpdateDto;
import com.wallet.base.ResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("")
    public ResponseDto<List<WalletResponseDto>> getWallets(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseDto.success("지갑 목록을 조회했습니다.", 200, walletService.getWallets(userId));
    }

    @PostMapping("")
    public ResponseDto<WalletResponseDto> createWallet(Authentication authentication, @RequestBody @Valid WalletCreateDto walletCreateDto) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseDto.success("지갑을 생성했습니다.", 201, walletService.createWallet(userId, walletCreateDto));
    }

    @PutMapping("/{walletId}")
    public ResponseDto<WalletResponseDto> updateWallet(Authentication authentication, @PathVariable Long walletId, @RequestBody @Valid WalletUpdateDto walletUpdateDto) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseDto.success("지갑을 수정했습니다.", 200, walletService.updateWallet(userId, walletId, walletUpdateDto));
    }

    @DeleteMapping("/{walletId}")
    public ResponseDto<String> deleteWallet(Authentication authentication, @PathVariable Long walletId) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseDto.success(walletService.deleteWallet(userId, walletId), 200 ,null);
    }

    // @GetMapping("/{walletId}/transactions")
    // public ResponseDto<List<TransactionResponseDto>> getTransactions(Authentication authentication, @PathVariable Long walletId) {
    //     Long userId = Long.parseLong(authentication.getName());
    //     return ResponseDto.success(walletService.getTransactions(userId, walletId), 200 ,null);
    // }
}
