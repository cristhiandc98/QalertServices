package qalert.com.models.payment;

import qalert.com.utils.enums.PaymentStatusEnum;

public class PaymentUpdateRequest {

    private long paymentId;
    private PaymentStatusEnum paymentStatusId;
    private String paymentError;

    public PaymentUpdateRequest() {
    }

    public PaymentUpdateRequest(long paymentId, PaymentStatusEnum paymentStatusId) {
        this(paymentId, paymentStatusId, null);
    }

    public PaymentUpdateRequest(long paymentId, PaymentStatusEnum paymentStatusId, String paymentError) {
        this.paymentId = paymentId;
        this.paymentStatusId = paymentStatusId;
        this.paymentError = paymentError;
    }

    public String getPaymentError() {
        return paymentError;
    }

    public void setPaymentError(String paymentError) {
        this.paymentError = paymentError;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatusEnum getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(PaymentStatusEnum paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }
}