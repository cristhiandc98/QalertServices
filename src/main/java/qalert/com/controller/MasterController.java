package qalert.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.IMaster;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.master.MasterResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.MASTER)
public class MasterController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IMaster service;
	

    @Autowired
    private ILogService logService;


	
    @GetMapping(value = ApiConst.GET_TERMS_AND_CONDITIONS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getTermsAndConditions(HttpServletRequest http) {

		LogServiceRequest logModel = logService.setRequestData(http);

		Response2<MasterResponse> out;

        try {
            out = new Response2<> (service.getTermsAndConditions());
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

		String termsAndConditions = out.getData().getValueVarchar();
		out.getData().setValueVarchar(null);
		logService.setResponseDataAndSave(logModel, out);
		out.getData().setValueVarchar(termsAndConditions);

        return ResponseEntity.status(out.getStatusCode()).body(out);
	}



    @GetMapping(value = ApiConst.GET_APP_SETTINGS, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> listAppSettings(HttpServletRequest http) {

		LogServiceRequest logModel = logService.setRequestData(http);

		Response2<List<MasterResponse>> out;

        try {
            out = new Response2<> (service.getAppSettingsList());
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
