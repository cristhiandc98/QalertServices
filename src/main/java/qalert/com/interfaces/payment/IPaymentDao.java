package qalert.com.interfaces.payment;

import qalert.com.models.payment.PaymentRequest;

public interface IPaymentDao {

    void insert(PaymentRequest request);

}
