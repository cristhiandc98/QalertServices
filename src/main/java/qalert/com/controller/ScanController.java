package qalert.com.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.scan.IScanService;
import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanHeaderResponse;
import qalert.com.models.scan.ScanRequest;
import qalert.com.models.scan.ScanResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.exceptions.ConflictException;
import qalert.com.utils.exceptions.InvalidFormException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.SCAN)
public class ScanController {

    @Autowired
    private IScanService scanService;

    @Autowired
    private ILogService logService;



    @PostMapping(value = ApiConst.GET_ADDITIVES_FROM_IMAGE, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getAdditivesFromImage(HttpServletRequest http, 
        @RequestParam MultipartFile image,
        @RequestParam String userId) {

        LogServiceRequest logModel = null;
        Response2<ScanResponse> out;

        try {
            scanService.isValidImageContent(image);


            Response2<String> additivesRsp = scanService.getAdditivesFromImage(image);
            

            logModel = logService.setRequestData(http, additivesRsp.getData());

            LocalDateTime beginDateTime = LocalDateTime.now();
            logModel.setBeginDateTime(beginDateTime);


            out = scanService.insertAndGetAdditivesFromPlainText(Long.parseLong(userId), additivesRsp.getData());

        } catch (InvalidFormException ex) {
            out = new Response2<>(ex);
            logModel = logService.setRequestData(http, userId);
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
            logModel = logService.setRequestData(http, userId);
        } catch (ConflictException ex) {
            out = new Response2<>(ex);
            logModel = logService.setRequestData(http, userId);
        } catch (Exception ex) {
            out = new Response2<>(ex);
            logModel = logService.setRequestData(http, userId);
        }


        logService.setResponseDataAndSave(logModel, out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}



    @PostMapping(produces = ApiConst.PRODUCES)
	public ResponseEntity<?> insert(HttpServletRequest http, @RequestBody ScanRequest request) {

		LogServiceRequest logModel = logService.setRequestData(http, request);
		
		Response2<String> out = null;

        try {
            request.validateInsert();
            
            request.setUserId(logModel.getUserId());
            
            scanService.insert(request);

            out = new Response2<>(HttpStatus.CREATED, "¡Escaneo guardado exitosamente!", true);

        } catch (InvalidFormException ex) {
            out = new Response2<>(ex);
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (ConflictException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

		logService.setResponseDataAndSave(logModel, out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}



    @PostMapping(value=ApiConst.GET_ADDITIVES_REPORT, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getAdditivesReport(HttpServletRequest http, @RequestBody ScanRequest request) {

        LogServiceRequest logModel = logService.setRequestData(http, request);
		
		Response2<ScanResponse> out = null;

        try {
            request.setUserId(logModel.getUserId());
            
            out = scanService.getAdditivesReport(request);

        } catch (InvalidFormException ex) {
            out = new Response2<>(ex);
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        } catch (ConflictException ex) {
            out = new Response2<>(ex);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

		logService.setResponseDataAndSave(logModel, out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}



    @GetMapping(value=ApiConst.GET_SCAN_LIST, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getScanList(HttpServletRequest http, @RequestParam Long profileId) {

		LogServiceRequest logModel = logService.setRequestData(http, null);

        Response2<List<ScanHeaderResponse>> out;

        if(profileId != null)
            out = scanService.getScanList(profileId);
        else 
            out = new Response2<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST, false);

		logService.setResponseDataAndSave(logModel, out);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
