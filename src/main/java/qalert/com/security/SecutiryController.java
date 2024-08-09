package qalert.com.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.Url;
import qalert.com.utils.consts.UserMessageConst;

@RestController
@RequestMapping(Url.SECURITY)
public class SecutiryController {

    @Qualifier(qalert.com.utils.consts.Consts.QALIFIER_SERVICE)
    @Autowired
    private ISeguridad service;

    @Qualifier(qalert.com.utils.consts.Consts.QALIFIER_SERVICE)
    @Autowired
    private IUser userService;
    
	private static final Logger logger = LogManager.getLogger(SecutiryController.class);
		
	@PostMapping(Url.VALIDATE_EMAIL)
	public ResponseEntity<?> validarCorreo(@RequestBody UserRequest request) {
		Response_<String> out = null;

		try {		
			if(request.validateUserName())
				out = service.saveVerificationCode(request);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, request)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out.getData());
	}

	@PostMapping(Url.CREATE_USER)
	public ResponseEntity<?> crear(@RequestBody UserRequest request) {
		Response_<String> out = null;

		try {		
			if(request.validateUserRegister())
				out = userService.insert(request);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, request)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out.getData());
	}
	
	@PostMapping(Url.RESET_DEVICE_ID)
	public ResponseEntity<?> resetIdDevice(@RequestBody UserRequest request) {
		Response_<String> out = null;

		try {		
			if(request.validateUserRegister())
				out = service.resetIdDevice(request);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, request)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out.getData());
	}

	@GetMapping(Url.ROOT)
    public ResponseEntity<?> root() {
        return ResponseEntity.ok("¡Service running!");
	}
}
