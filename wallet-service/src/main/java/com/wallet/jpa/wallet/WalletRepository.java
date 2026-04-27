package com.wallet.jpa.wallet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    List<WalletEntity> findAllByUserId(Long userId);

    List<WalletEntity> findAllByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
