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
import qalert.com.interfaces.ILogService;
import qalert.com.interfaces.IMaster;
import qalert.com.models.generic.Response_;
import qalert.com.models.master.MasterResponse;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.MASTER)
public class MasterController {

    @Qualifier(qalert.com.utils.consts.CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IMaster service;
	

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ILogService serviceLog;


    @GetMapping(value = ApiConst.GET_TERMS_AND_CONDITIONS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getTermsAndConditions(HttpServletRequest http) {
		serviceLog.setRequestData(http, null);

		Response_<MasterResponse> out = service.getTermsAndConditions();

		serviceLog.savePrivate(out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}


    @GetMapping(value = ApiConst.LIST_APP_SETTINGS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> listAppSettings(HttpServletRequest http) {
		serviceLog.setRequestData(http, null);

		Response_<List<MasterResponse>> out = service.listAppSettings();
	
		serviceLog.save(out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
