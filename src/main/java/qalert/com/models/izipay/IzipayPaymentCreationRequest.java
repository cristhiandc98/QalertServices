package qalert.com.models.izipay;

import qalert.com.models.payment.PaymentCreationResponse;

public class IzipayPaymentCreationRequest{

    private String orderId;

    private String currency;

    private Integer amount;

    public IzipayPaymentCreationRequest() {
    }

    public IzipayPaymentCreationRequest(PaymentCreationResponse request) {
        orderId = request.getOrderId();
        currency = request.getCurrency();
        amount = request.getAmount().intValue();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
