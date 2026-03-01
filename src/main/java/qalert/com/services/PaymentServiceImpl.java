package qalert.com.services;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.payment.IPaymentDao;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.models.izipay.IzipayPaymentCreationRequest;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationResponse;
import qalert.com.models.payment.PaymentUpdateRequest;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.EnvironmentConst;
import qalert.com.utils.enums.PaymentStatusEnum;
import qalert.com.utils.exceptions.ConflictException;
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



    @Override
    public PaymentCreationResponse insert(PaymentCreationRequest request) {
        return paymentDao.insert(request);
    }



    @Override
    public void update(PaymentUpdateRequest request) {
        paymentDao.update(request);
    }



    @Override
    public IzipayPaymentCreationResponse insertAndGenerateUrl(PaymentCreationRequest request) throws JsonProcessingException {

        IzipayPaymentCreationResponse izipayRsp = null;

        PaymentCreationResponse paymentRsp = paymentDao.insert(request);

        String auth = Base64.getEncoder()
                .encodeToString((env.getRequiredProperty(EnvironmentConst.IZIPAY_API_USER) + ":" + env.getRequiredProperty(EnvironmentConst.IZIPAY_API_PASSWORD))
                    .getBytes(StandardCharsets.UTF_8));

        izipayRsp = webClient.post()
                .uri(env.getRequiredProperty(EnvironmentConst.IZIPAY_API_CREATEPAYMENT))
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .bodyValue(new IzipayPaymentCreationRequest(paymentRsp))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    
                                    update(new PaymentUpdateRequest(paymentRsp.getPaymentId(), PaymentStatusEnum.ERROR_GENERATE_URL, body));
                                    
                                    return Mono.error(new ConflictException("No se pudo generar la URL de pago", body));
                                })
                )
                .bodyToMono(IzipayPaymentCreationResponse.class)
                .block();

        if(izipayRsp != null && izipayRsp.getStatus().equals("ERROR")){
            update(new PaymentUpdateRequest(paymentRsp.getPaymentId(), PaymentStatusEnum.ERROR_GENERATE_URL, objectMapper.writeValueAsString(izipayRsp)));
        }

        return izipayRsp;
    }


}
