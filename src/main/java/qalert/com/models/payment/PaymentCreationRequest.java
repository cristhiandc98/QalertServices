package qalert.com.models.payment;

public class PaymentCreationRequest {

    private Long userId;

    private Integer subscriptionId;

    public PaymentCreationRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
