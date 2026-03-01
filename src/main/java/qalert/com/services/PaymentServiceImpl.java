package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import qalert.com.dao.PaymentDaoImpl;
import qalert.com.interfaces.payment.IPaymentDao;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.interfaces.profile.IProfileDao;
import qalert.com.models.payment.PaymentRequest;

@Service
public class PaymentServiceImpl implements IPaymentService{

    @Autowired
    private IPaymentDao paymentDao;

    @Override
    public void insert(PaymentRequest request) {
        paymentDao.insert(request);
    }


}
