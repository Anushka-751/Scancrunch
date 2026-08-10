package com.scanCrunch.domain.payment.dto;

public class PaymentSummaryResponse {

    private Long orderId;
    private Double totalAmount;
    private String status;

    public PaymentSummaryResponse() {
    }

    public PaymentSummaryResponse(Long orderId, Double totalAmount, String status) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}