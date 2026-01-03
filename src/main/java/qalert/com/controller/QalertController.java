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

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.IQalert;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.subscription.SubscriptionModel;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.QALERT)
public class QalertController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IQalert service;

    @Autowired
    private ILogService logService;

    @PostMapping(value = ApiConst.SUBSCRIBE, produces = ApiConst.PRODUCES)
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionModel request, HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<Boolean> out;

        try {
            service.subscribe(logModel.getUserId(), request); // SIN retorno
            out = new Response2<>();
            out = new Response2<>(HttpStatus.OK, "Suscripción realizada correctamente", true);
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

}
