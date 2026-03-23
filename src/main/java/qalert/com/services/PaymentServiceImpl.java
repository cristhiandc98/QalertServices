package qalert.com.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import qalert.com.interfaces.payment.IPaymentDao;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.models.izipay.IzipayPaymenGetRequest;
import qalert.com.models.izipay.IzipayPaymenGetResponse;
import qalert.com.models.izipay.IzipayPaymentCreationRequest;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationResponse;
import qalert.com.models.payment.PaymentGetResponse;
import qalert.com.models.payment.PaymentUpdateRequest;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.EnvironmentConst;
import qalert.com.utils.enums.PaymentStatusEnum;
import qalert.com.utils.exceptions.ConflictException;
import qalert.com.utils.exceptions.WebServiceException;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements IPaymentService{

    @Autowired
    private IPaymentDao paymentDao;

    @Autowired
	private Environment env;

    @Autowired
    @Qualifier(CommonConsts.QALIFIER_IZIPAY_WEB_CLIENT)
	private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;



    private String getIzipayCredentials(){
        return "Basic " + Base64.getEncoder()
                .encodeToString((env.getRequiredProperty(EnvironmentConst.IZIPAY_API_USER) + ":" + env.getRequiredProperty(EnvironmentConst.IZIPAY_API_PASSWORD))
                    .getBytes(StandardCharsets.UTF_8));
    }



    @Override
    public PaymentCreationResponse insert(PaymentCreationRequest request) {
        return paymentDao.insert(request);
    }



    @Override
    public void update(PaymentUpdateRequest request) {
        paymentDao.update(request);
    }



    @Override
    public PaymentGetResponse get(String paymentCode) {
        return paymentDao.get(paymentCode);
    }



    @Override
    public IzipayPaymentCreationResponse generateIzipayUrl(IzipayPaymentCreationRequest request, Long paymentId) throws JsonProcessingException {

        IzipayPaymentCreationResponse izipayRsp = null;

        izipayRsp = webClient.post()
                .uri(env.getRequiredProperty(EnvironmentConst.IZIPAY_API_CREATE_PAYMENT))
                .header(HttpHeaders.AUTHORIZATION, getIzipayCredentials())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    return Mono.error(new WebServiceException("No se pudo generar la URL de pago", request, body));
                                })
                )
                .bodyToMono(IzipayPaymentCreationResponse.class)
                .block();
    

        if(izipayRsp != null && (izipayRsp.getStatus().equals("ERROR") 
                    || izipayRsp.getAnswer().getErrorCode() != null))
            throw new WebServiceException("Etiqueta ERROR de izipay", request, izipayRsp);

        return izipayRsp;
    }



    @Override
    public IzipayPaymenGetResponse getIzipayPayment(IzipayPaymenGetRequest request) throws JsonProcessingException {

        IzipayPaymenGetResponse izipayRsp = null;

        izipayRsp = webClient.post()
                .uri(env.getRequiredProperty(EnvironmentConst.IZIPAY_API_GET_PAYMENT))
                .header(HttpHeaders.AUTHORIZATION, getIzipayCredentials())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {     
                                    return Mono.error(new WebServiceException("No se pudo obtener los datos del pago", request, body));
                                })
                )
                .bodyToMono(IzipayPaymenGetResponse.class)
                .block();

        if(izipayRsp != null && izipayRsp.getAnswer().getErrorCode() != null)
            throw new WebServiceException("Error al obtener los datos de pago de iziapay", request, izipayRsp);

        return izipayRsp;
    }
    
}
