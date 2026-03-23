package qalert.com.interfaces;

import java.util.List;

import qalert.com.models.subscription.SubscriptionResponse;

public interface ISubscription {

    List<SubscriptionResponse> getSubscriptions();

}
