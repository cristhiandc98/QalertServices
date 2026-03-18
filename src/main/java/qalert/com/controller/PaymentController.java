package qalert.com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.models.generic.Response2;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.izipay.IzipayPaymentUpdateRequest;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentUpdateRequest;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.subscription.SubscriptionLabelsResponse;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.enums.PaymentStatusEnum;
import qalert.com.utils.exceptions.ConflictException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.PAYMENT)
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private ILogService logService;

    private final SimpMessagingTemplate messagingTemplate;


    
    public PaymentController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }



    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> subscribe(@RequestBody PaymentCreationRequest request, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<IzipayPaymentCreationResponse> out;

        try {
            IzipayPaymentCreationResponse izipayModel = paymentService.insertAndGenerateUrl(request);
            
            out = new Response2<>(izipayModel);

        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (ConflictException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }



    @PostMapping("/update")
    public ResponseEntity<?> izipayWebhook(
            @RequestBody Map<String, Object> request, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<Map<String, Object>> out;

        try {
            messagingTemplate.convertAndSend(
                    ApiConst.WS_CLIENT_PREFIX + "/" + 1,
                    new Response2<>(request)
            );

            out = new Response2<>();

        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (ConflictException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        out.setData(request);
        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.ok().build();
    }



    // @PostMapping("/update")
    // public ResponseEntity<?> receiveWebhook(@RequestBody IzipayPaymentUpdateRequest request){

    //     paymentService.update(new PaymentUpdateRequest(request.getPaymentId(), PaymentStatusEnum.fromId(request.getPaymentStatusId()), null));

    //     messagingTemplate.convertAndSend(
    //             ApiConst.WS_CLIENT_PREFIX + "/" + request.getPaymentId(),
    //             new Response2<>(true)
    //     );

    //     return ResponseEntity.ok().build();
    // }

}
