package qalert.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.USER)
public class UserController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IUser service;

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ILogService serviceLog;
	


    @PostMapping(produces = ApiConst.PRODUCES)
	public ResponseEntity<?> insert(HttpServletRequest http, @RequestBody UserRequest request) {
		LogServiceRequest logModel = serviceLog.setRequestData(http, request, null, true);
		
		Response2<String> out;

		String error;
		if((error = request.validateUserRegister()) == null)
			out = service.insert(request);
		else
			out = new Response2<>(HttpStatus.BAD_REQUEST, error);

		serviceLog.setResponseDataAndSave(logModel, out, true);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

    @PostMapping(value = ApiConst.UPDATE_PASSWORD, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> updatePassword(HttpServletRequest http, @RequestBody UserRequest request) {
		LogServiceRequest logModel = serviceLog.setRequestData(http, request, null, true);

		Response2<String> out;

		String error;
		if((error = request.validateUpdatePassword()) == null)
			out = service.updatePassword(request);
		else
			out = new Response2<>(HttpStatus.BAD_REQUEST, error);

		serviceLog.setResponseDataAndSave(logModel, out, false);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
