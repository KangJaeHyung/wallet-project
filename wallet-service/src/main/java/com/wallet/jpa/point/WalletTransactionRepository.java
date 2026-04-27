package com.wallet.jpa.point;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {

    List<WalletTransactionEntity> findAllByWalletIdOrderByIdDesc(Long walletId);

    Optional<WalletTransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
