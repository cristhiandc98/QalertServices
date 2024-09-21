package qalert.com.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import qalert.com.interfaces.IMaster;
import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.master.MasterResponse;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.UserMessageConst;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.MASTER)
public class MasterController {

    @Qualifier(qalert.com.utils.consts.Consts.QALIFIER_SERVICE)
    @Autowired
    private IMaster service;
    
	private static final Logger logger = LogManager.getLogger(UserController.class);

    @GetMapping(value = ApiConst.GET_TERMS_AND_CONDITIONS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getTermsAndConditions() {
		Response_<MasterResponse> out = null;

		try {		
            out = service.getTermsAndConditions();
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, null)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

    @GetMapping(value = ApiConst.LIST_APP_SETTINGS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> listAppSettings() {
		Response_<List<MasterResponse>> out = null;

		try {		
            out = service.listAppSettings();
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, null)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
