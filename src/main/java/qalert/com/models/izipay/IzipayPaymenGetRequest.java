package qalert.com.models.izipay;

public class IzipayPaymenGetRequest {

    private String paymentOrderId;

    public IzipayPaymenGetRequest() {
    }

    public IzipayPaymenGetRequest(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }
    
}
