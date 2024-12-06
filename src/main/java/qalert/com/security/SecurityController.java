package qalert.com.security;

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

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.SECURITY)
public class SecurityController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ISecurity securityService;

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ILogService serviceLog;
	
		
	@PostMapping(ApiConst.GET_VERIFICATION_CODE)
	public ResponseEntity<?> getVerificationCode(HttpServletRequest http, @RequestBody UserRequest request, @RequestParam boolean isChangeDevice) {
		LogServiceRequest logModel = serviceLog.setRequestData(http, request, null, false);
		
		Response2<String> out;
		
		String error;
		if((error = request.validateFormVerificationCode()) == null)
			out = securityService.saveVerificationCode(request, isChangeDevice);
		else
			out = new Response2<>(HttpStatus.BAD_REQUEST, error);

		serviceLog.setResponseDataAndSave(logModel, out, false);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

	@GetMapping(ApiConst.ROOT)
    public ResponseEntity<?> root() {
        return ResponseEntity.ok("¡Service running!");
	}
}
