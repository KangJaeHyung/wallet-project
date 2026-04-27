CREATE DATABASE IF NOT EXISTS wallet_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE wallet_db;

DROP TABLE IF EXISTS payment_events;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS wallet_transactions;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS idempotency_keys;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE wallets (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    wallet_name VARCHAR(50) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    color_code VARCHAR(20) NOT NULL DEFAULT '#4F46E5',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_wallets_user_id (user_id),
    KEY idx_wallets_status (status),
    CONSTRAINT fk_wallets_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE wallet_transactions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    wallet_id BIGINT UNSIGNED NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    reference_type VARCHAR(30) NULL,
    reference_id VARCHAR(64) NULL,
    description VARCHAR(255) NULL,
    idempotency_key VARCHAR(100) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_wallet_transactions_wallet_id (wallet_id),
    KEY idx_wallet_transactions_type (transaction_type),
    KEY idx_wallet_transactions_reference (reference_type, reference_id),
    KEY idx_wallet_transactions_created_at (created_at),
    UNIQUE KEY uk_wallet_transactions_idempotency_key (idempotency_key),
    CONSTRAINT fk_wallet_transactions_wallet_id
        FOREIGN KEY (wallet_id) REFERENCES wallets (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE refresh_tokens (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    client_type VARCHAR(20) NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_token (token),
    UNIQUE KEY uk_refresh_tokens_user_id_client_type_device_id (user_id, client_type, device_id),
    KEY idx_refresh_tokens_user_id (user_id),
    KEY idx_refresh_tokens_expires_at (expires_at),
    CONSTRAINT fk_refresh_tokens_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE idempotency_keys (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NULL,
    request_key VARCHAR(100) NOT NULL,
    request_hash VARCHAR(255) NOT NULL,
    resource_type VARCHAR(30) NOT NULL,
    response_code VARCHAR(20) NULL,
    response_body TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_idempotency_keys_request_key (request_key),
    KEY idx_idempotency_keys_user_id (user_id),
    KEY idx_idempotency_keys_resource_type (resource_type),
    CONSTRAINT fk_idempotency_keys_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payments (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    wallet_id BIGINT UNSIGNED NULL,
    order_id VARCHAR(64) NOT NULL,
    amount BIGINT NOT NULL,
    payment_method VARCHAR(30) NOT NULL DEFAULT 'MOCK_PG',
    status VARCHAR(20) NOT NULL DEFAULT 'READY',
    approved_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_order_id (order_id),
    KEY idx_payments_user_id (user_id),
    KEY idx_payments_wallet_id (wallet_id),
    KEY idx_payments_status (status),
    KEY idx_payments_created_at (created_at),
    CONSTRAINT fk_payments_user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_payments_wallet_id
        FOREIGN KEY (wallet_id) REFERENCES wallets (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payment_events (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    payment_id BIGINT UNSIGNED NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    event_status VARCHAR(20) NOT NULL,
    payload TEXT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_payment_events_payment_id (payment_id),
    KEY idx_payment_events_event_type (event_type),
    KEY idx_payment_events_created_at (created_at),
    CONSTRAINT fk_payment_events_payment_id
        FOREIGN KEY (payment_id) REFERENCES payments (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;
