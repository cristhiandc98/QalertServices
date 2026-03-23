package qalert.com.models.payment;

public class PaymentUpdateRequest {

    private long paymentId;
    private String paymentOrderId;
    private String paymentStatusCode;
    private String paymentError;

    public PaymentUpdateRequest() {
    }

    public PaymentUpdateRequest(long paymentId, String paymentOrderId, String paymentStatusName, String paymentError) {
        this.paymentId = paymentId;
        this.paymentOrderId = paymentOrderId;
        this.paymentStatusCode = paymentStatusName;
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

    public String getPaymentStatusCode() {
        return paymentStatusCode;
    }

    public void setPaymentStatusCode(String paymentStatusName) {
        this.paymentStatusCode = paymentStatusName;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }
}