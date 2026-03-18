package qalert.com.interfaces;

import java.util.List;

import qalert.com.models.subscription.SubscriptionLabelsResponse;

public interface ISubscription {

    List<SubscriptionLabelsResponse> getAll();

}
