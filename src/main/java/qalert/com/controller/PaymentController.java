package qalert.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.payment.IPaymentService;
import qalert.com.models.generic.Response2;
import qalert.com.models.izipay.IzipayPaymentCreationResponse;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.subscription.SubscriptionModel;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.exceptions.ConflictException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.PAYMENT)
public class PaymentController {

    @Autowired
    private IPaymentService service;

    @Autowired
    private ILogService logService;



    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> subscribe(@RequestBody PaymentCreationRequest request, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<IzipayPaymentCreationResponse> out;

        try {
            IzipayPaymentCreationResponse izipayModel = service.insertAndGenerateUrl(request);
            
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

}
