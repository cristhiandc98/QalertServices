package qalert.com.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ISubscription;
import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.models.generic.Response2;
import qalert.com.models.izipay.IzipayPaymenGetRequest;
import qalert.com.models.izipay.IzipayPaymenGetResponse;
import qalert.com.models.izipay.IzipayPaymentCreationRequest;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationResponse;
import qalert.com.models.payment.PaymentGetResponse;
import qalert.com.models.payment.PaymentUpdateRequest;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.enums.PaymentStatusEnum;
import qalert.com.utils.exceptions.WebServiceException;

@RestController
@RequestMapping(ApiConst.PAYMENT)
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    private ISubscription subscriptionService;

    @Autowired
    private ILogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    private final SimpMessagingTemplate messagingTemplate;


    
    public PaymentController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }



    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> subscribe(@RequestBody PaymentCreationRequest request, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<IzipayPaymentCreationResponse> out;

        Long paymentId = null;
        String paymentOrderId = null;
        String paymentStatusName;

        try {
            PaymentCreationResponse paymentRsp = paymentService.insert(request);
            paymentId = paymentRsp.getPaymentId();

            IzipayPaymentCreationResponse izipayRsp = paymentService.generateIzipayUrl(new IzipayPaymentCreationRequest(paymentRsp), paymentRsp.getPaymentId());
            paymentOrderId = izipayRsp.getAnswer().getPaymentOrderId();

            paymentStatusName = PaymentStatusEnum.PENDING.toString();


            out = new Response2<>(izipayRsp);

        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
            paymentStatusName = PaymentStatusEnum.FAILED.toString();

        } catch (WebServiceException ex) {
            out = new Response2<>(ex, objectMapper);
            paymentStatusName = PaymentStatusEnum.ERROR_GENERATE_URL.toString();

        } catch (Exception ex) {
            out = new Response2<>(ex);
            paymentStatusName = PaymentStatusEnum.FAILED.toString();
        }

        //if payment is created in bd
        if(paymentId != null)
            paymentService.update(new PaymentUpdateRequest(paymentId, paymentOrderId, paymentStatusName, out.getErrorMssg() ));


        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }



    @PostMapping(value = "/update", consumes = "*/*")
    public ResponseEntity<?> izipayWebhook(HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, null);
        
        Response2<Boolean> out;

        Long paymentId = null;
        String paymentStatusCode = PaymentStatusEnum.FAILED.toString();
        boolean signed = false;

        try {
            //get payment code
            String krAnswer = http.getParameter("kr-answer");
            Map<String, Object> json = objectMapper.readValue(krAnswer, Map.class);
            Map orderDetails = (Map) json.get("orderDetails");
            String paymentCode = (String) orderDetails.get("orderId");

            logModel = logService.setRequestData(http, paymentCode);
            

            PaymentGetResponse paymentRsp = paymentService.get(paymentCode);
            paymentId = paymentRsp.getPaymentId();

            IzipayPaymenGetResponse iziRsp = paymentService.getIzipayPayment(new IzipayPaymenGetRequest(paymentRsp.getPaymentOrderId()));
            paymentStatusCode = iziRsp.getAnswer().getPaymentOrderStatus();

            if(PaymentStatusEnum.PAID.toString().equals(paymentStatusCode)){
                subscriptionService.updateUserSubscription(paymentRsp.getUserId());
                signed = true;
            }

            messagingTemplate.convertAndSend(ApiConst.WS_CLIENT_PREFIX + "/" + paymentCode, signed);

            out = new Response2<>();

        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        
        } catch (WebServiceException ex) {
            out = new Response2<>(ex, objectMapper);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        //if payment is created in bd
        if(paymentId != null)
            paymentService.update(new PaymentUpdateRequest(paymentId, null, paymentStatusCode, out.getErrorMssg() ));
        
        
        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.ok().build();
    }



    @GetMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> abandoned(@RequestParam("paymentCode") String paymentCode, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, paymentCode);

        Response2<PaymentGetResponse> out;

        try {
            out = new Response2<>(paymentService.get(paymentCode));

        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }
        
        
        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }
}
