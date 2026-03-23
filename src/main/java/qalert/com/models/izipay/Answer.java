package qalert.com.models.izipay;

public class Answer {

    private String orderId;
    private String paymentOrderId;

    private String paymentURL;
    private String paymentOrderStatus;

    private String errorCode;
    private String errorMessage;
    private String detailedErrorMessage;


    public Answer() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDetailedErrorMessage() {
        return detailedErrorMessage;
    }

    public void setDetailedErrorMessage(String detailedErrorMessage) {
        this.detailedErrorMessage = detailedErrorMessage;
    }

    public String getPaymentOrderStatus() {
        return paymentOrderStatus;
    }

    public void setPaymentOrderStatus(String paymentOrderStatus) {
        this.paymentOrderStatus = paymentOrderStatus;
    }
}
