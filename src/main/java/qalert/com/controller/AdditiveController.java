package qalert.com.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.IAdditive;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.additive.AdditiveResponse;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.ADDITIVE)
public class AdditiveController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IAdditive additiveService;

    @Autowired
    private ILogService logService;

    @GetMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> lisAdditive(HttpServletRequest http) {

        LogServiceRequest logModel = logService.setRequestData(http);
        
        Response2<List<AdditiveResponse>> out;

        out = additiveService.lisAdditive();

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);

    }
    
}
