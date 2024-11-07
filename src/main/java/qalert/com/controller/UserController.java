package qalert.com.controller;

import org.springframework.web.bind.annotation.RestController;

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.UserMessageConst;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;

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
		serviceLog.setRequestData(http, request);
		Response_<String> out = null;

		try {		
			if(request.validateUserRegister())
				out = service.insert(request);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            out = new Response_<>(e);
		}

		serviceLog.save(out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

    @PostMapping(value = ApiConst.UPDATE_PASSWORD, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> updatePassword(@RequestBody UserRequest request) {
		Response_<String> out = null;

		try {		
			if(request.validateUpdatePassword())
				out = service.updatePassword(request);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            //logger.error((out = new Response_<>(e, request)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
