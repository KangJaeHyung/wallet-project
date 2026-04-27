package com.wallet.api.wallet;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wallet.api.wallet.dto.WalletCreateDto;
import com.wallet.api.wallet.dto.WalletResponseDto;
import com.wallet.api.wallet.dto.WalletUpdateDto;
import com.wallet.base.exception.BusinessException;
import com.wallet.base.exception.ErrorCode;
import com.wallet.jpa.wallet.WalletEntity;
import com.wallet.jpa.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    private static final List<String> DEFAULT_PASTEL_COLORS = List.of(
        "#F8BBD0",
        "#FFE0B2",
        "#FFF9C4",
        "#C8E6C9",
        "#B3E5FC",
        "#D1C4E9",
        "#F5E1DA",
        "#DCE775"
    );

    private String resolveColorCode(String colorCode) {
        try {
        if (colorCode != null && !colorCode.isBlank()) {
                return colorCode;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(DEFAULT_PASTEL_COLORS.size());
        return DEFAULT_PASTEL_COLORS.get(randomIndex);
        } catch (Exception e) {
            return DEFAULT_PASTEL_COLORS.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDto> getWallets(Long userId) {
        return walletRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, "ACTIVE").stream()
                .map(wallet -> WalletResponseDto.builder()
                        .id(wallet.getId())
                        .walletName(wallet.getWalletName())
                        .balance(wallet.getBalance())
                        .colorCode(wallet.getColorCode())
                        .status(wallet.getStatus())
                        .createdAt(wallet.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public WalletResponseDto createWallet(Long userId, WalletCreateDto walletCreateDto) {

        String colorCode = resolveColorCode(walletCreateDto.getColorCode());
        WalletEntity wallet = WalletEntity.builder()
                .userId(userId)
                .walletName(walletCreateDto.getWalletName().trim())
                .balance(0L)
                .colorCode(colorCode)
                .status("ACTIVE")
                .build();

        WalletEntity savedWallet = walletRepository.save(wallet);

        return WalletResponseDto.builder()
                .id(savedWallet.getId())
                .walletName(savedWallet.getWalletName())
                .balance(savedWallet.getBalance())
                .colorCode(savedWallet.getColorCode())
                .status(savedWallet.getStatus())
                .createdAt(savedWallet.getCreatedAt())
                .build();
    }

    public WalletResponseDto updateWallet(Long userId, Long walletId, WalletUpdateDto walletUpdateDto) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));
        wallet.setWalletName(walletUpdateDto.getWalletName().trim());
        wallet.setColorCode(resolveColorCode(walletUpdateDto.getColorCode()));
        WalletEntity savedWallet = walletRepository.save(wallet);
        return WalletResponseDto.builder()
                .id(savedWallet.getId())
                .walletName(savedWallet.getWalletName())
                .balance(savedWallet.getBalance())
                .colorCode(savedWallet.getColorCode())
                .status(savedWallet.getStatus())
                .createdAt(savedWallet.getCreatedAt())
                .build();
    }

    @Transactional
    public String deleteWallet(Long userId, Long walletId) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND));
        if (!wallet.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (wallet.getBalance() > 0) {
            throw new BusinessException(ErrorCode.WALLET_HAS_BALANCE);
        }
        wallet.setStatus("INACTIVE");
        walletRepository.save(wallet);
        return "지갑을 삭제했습니다.";
    }
}
