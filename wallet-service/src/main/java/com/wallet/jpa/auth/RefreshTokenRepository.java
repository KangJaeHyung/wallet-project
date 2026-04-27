package com.wallet.jpa.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByUserIdAndClientTypeAndDeviceId(Long userId, String clientType, String deviceId);
}
