package com.scanCrunch.domain.receipt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanCrunch.domain.receipt.entity.PaymentReceipt;

@Repository
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {

    Optional<PaymentReceipt> findByPaymentId(String paymentId);

    boolean existsByPaymentId(String paymentId);
}
