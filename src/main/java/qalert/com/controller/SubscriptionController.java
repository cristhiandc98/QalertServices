package qalert.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ISubscription;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.subscription.SubscriptionResponse;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@RestController
@RequestMapping(ApiConst.SUBSCRIPTION)
public class SubscriptionController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ISubscription service;
	

    @Autowired
    private ILogService logService;


	
    @GetMapping(produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getSubscriptions(HttpServletRequest http) {

		LogServiceRequest logModel = logService.setRequestData(http);

		Response2<List<SubscriptionResponse>> out;

        try {
            out = new Response2<> (service.getSubscriptions());
            
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

		logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
	}
}
