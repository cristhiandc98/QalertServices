package qalert.com.models.izipay;

public class IzipayPaymentUpdateRequest {

    private Long paymentId;

    private int paymentStatusId;

    public IzipayPaymentUpdateRequest() {
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public int getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(int paymentStatusEnum) {
        this.paymentStatusId = paymentStatusEnum;
    }

}
