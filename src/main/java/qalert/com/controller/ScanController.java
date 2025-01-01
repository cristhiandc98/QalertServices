package qalert.com.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;

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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.interfaces.scan.IScanService;
import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanRequest;
import qalert.com.models.scan.ScanResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.utils.RegexUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(ApiConst.SCAN)
public class ScanController {

    @Autowired
    private IScanService scanService;

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ILogService logService;


    @PostMapping(value = ApiConst.GET_ADDITIVES_FROM_IMAGE, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> getAdditivesFromImage(HttpServletRequest http, 
        @RequestParam("file") MultipartFile image,
        @RequestParam("data") String data) {

        Response2<ScanResponse> out;
        LogServiceRequest logModel;
        int profileId = 0;

        if(RegexUtil.NUMBER.matcher(data).matches() && isValidImageContent(image)){
            
            LocalDateTime beginDateTime = LocalDateTime.now();
            profileId = Integer.parseInt(data);

            Response2<String> additivesRsp = scanService.getAdditivesFromImage(image);

            logModel = logService.setRequestData(http, additivesRsp.getData(), profileId, false);
            
            if(additivesRsp.isStatus())
                out = scanService.insertAndGetAdditivesFromPlainText(profileId, additivesRsp.getData());
            else 
                out = new Response2<>(additivesRsp);

            logModel.setBeginDateTime(beginDateTime);

        }else{

            logModel = logService.setRequestData(http, data, profileId, false);

            out = new Response2<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);
        }

        logService.setResponseDataAndSave(logModel, out, false);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

    public boolean isValidImageContent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null; 
        } catch (IOException e) {
            return false; 
        }
    }


    @PostMapping(produces = ApiConst.PRODUCES)
	public ResponseEntity<?> insert(HttpServletRequest http, @RequestBody ScanRequest request) {
		LogServiceRequest logModel = logService.setRequestData(http, request, request.getProfileId(), false);
		
		Response2<Boolean> out = request.validateInsert();

        if(out.isStatus())
            out = scanService.insert(request);

		logService.setResponseDataAndSave(logModel, out, false);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}


    @GetMapping(value=ApiConst.GET_ADDITIVES_REPORT, produces = ApiConst.PRODUCES)
	public ResponseEntity<?> insert(HttpServletRequest http, @RequestParam Integer profileId, @RequestParam Integer reportType) {
        
        ScanRequest request = new ScanRequest();
        request.setProfileId(profileId);
        request.setReportType(reportType);

		LogServiceRequest logModel = logService.setRequestData(http, request, profileId, false);
		
		Response2<ScanResponse> out = request.validateGetAdditiveReport();

        if(out.isStatus())
            out = scanService.getAdditivesReport(request);

		logService.setResponseDataAndSave(logModel, out, false);

		return ResponseEntity.status(out.getStatusCode()).body(out);
	}

}
