package qalert.com.security;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.UserMessageConst;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.SECURITY)
public class SecurityController {

    @Qualifier(qalert.com.utils.consts.CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ISeguridad service;
    
	private static final Logger logger = LogManager.getLogger(SecurityController.class);
		
	@PostMapping(ApiConst.GET_VERIFICATION_CODE)
	public ResponseEntity<?> getVerificationCode(@RequestBody UserRequest request, @RequestParam boolean isChangeDevice) {
		Response_<String> out = null;

		try {		
			if(request.validateFormVerificationCode())
				out = service.saveVerificationCode(request, isChangeDevice);
			else
				out = new Response_<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
		} catch (Exception e) {
            logger.error((out = new Response_<>(e, request)).getErrorMssg());
		}

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}
	
	@PostMapping(ApiConst.RESET_DEVICE_ID)
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

	@GetMapping(ApiConst.ROOT)
    public ResponseEntity<?> root() {
        return ResponseEntity.ok("¡Service running!");
	}
}
