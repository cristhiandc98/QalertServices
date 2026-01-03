package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IQalert;
import qalert.com.models.subscription.SubscriptionModel;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class QalertServiceImpl implements IQalert {

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private IQalert dao;

    @Override
    public void subscribe(Long user_id, SubscriptionModel request) {
        dao.subscribe(user_id,request);
    }



}
