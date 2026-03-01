package qalert.com.models.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequest {

    private Long paymentId;
    private Long userId;

    private BigDecimal amount;
    private Integer currencyId;

    private Integer paymentStatusId;

    private String izipayOrderId;
    private String izipayTransactionId;
    private String izipayFormToken;

    private String paymentDescription;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentRequest() {
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Integer paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getIzipayOrderId() {
        return izipayOrderId;
    }

    public void setIzipayOrderId(String izipayOrderId) {
        this.izipayOrderId = izipayOrderId;
    }

    public String getIzipayTransactionId() {
        return izipayTransactionId;
    }

    public void setIzipayTransactionId(String izipayTransactionId) {
        this.izipayTransactionId = izipayTransactionId;
    }

    public String getIzipayFormToken() {
        return izipayFormToken;
    }

    public void setIzipayFormToken(String izipayFormToken) {
        this.izipayFormToken = izipayFormToken;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
