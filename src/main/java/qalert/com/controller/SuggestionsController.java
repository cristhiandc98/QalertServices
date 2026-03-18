package qalert.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ISuggestions;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.suggestions.SuggestionsModel;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;

@RestController
@RequestMapping(ApiConst.SUGGESTIONS)
public class SuggestionsController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private ISuggestions suggestionsService;

    @Autowired
    private ILogService logService;


    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> insert(HttpServletRequest http, @RequestBody SuggestionsModel request) {
     LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<Boolean> out;

         String error;
         if ((error = request.validateInsert()) == null) {
            out = suggestionsService.insert(request);
         } else {
            out = new Response2<>(HttpStatus.BAD_REQUEST, error, false);
         }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

}
