package qalert.com.interfaces.payment;

import qalert.com.models.izipay.IzipayPaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationResponse;
import qalert.com.models.payment.PaymentUpdateRequest;

public interface IPaymentDao {

    PaymentCreationResponse insert(PaymentCreationRequest request);

    void update(PaymentUpdateRequest request);

}
