package qalert.com.interfaces;

import qalert.com.models.subscription.SubscriptionModel;

public interface IQalert {

    void subscribe(Long user_id,SubscriptionModel request);

}
