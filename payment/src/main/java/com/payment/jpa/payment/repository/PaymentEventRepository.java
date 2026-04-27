package com.payment.jpa.payment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.jpa.payment.entity.PaymentEventEntity;

public interface PaymentEventRepository extends JpaRepository<PaymentEventEntity, Long> {

    List<PaymentEventEntity> findAllByPaymentIdOrderByIdAsc(Long paymentId);
}
