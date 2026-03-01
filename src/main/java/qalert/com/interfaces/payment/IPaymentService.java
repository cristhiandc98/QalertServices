package qalert.com.interfaces.payment;

import com.fasterxml.jackson.core.JsonProcessingException;

import qalert.com.models.izipay.IzipayPaymentCreationRequest;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.payment.PaymentCreationRequest;

public interface IPaymentService extends IPaymentDao{

    IzipayPaymentCreationResponse insertAndGenerateUrl(PaymentCreationRequest request) throws JsonProcessingException;

}
