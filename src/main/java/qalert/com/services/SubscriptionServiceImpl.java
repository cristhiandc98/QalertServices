package qalert.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.ISubscription;
import qalert.com.models.subscription.SubscriptionLabelsResponse;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class SubscriptionServiceImpl implements ISubscription{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private ISubscription dao;

    @Override
    public List<SubscriptionLabelsResponse> getAll() {
        return dao.getAll();
    }

}
