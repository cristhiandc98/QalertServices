package qalert.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.IUser;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.exceptions.InvalidFormException; 

@RestController
@RequestMapping(ApiConst.USER)
public class UserController {

    @Qualifier(CommonConsts.QALIFIER_SERVICE)
    @Autowired
    private IUser service;

    @Autowired
    private ILogService serviceLog;

    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> insert(HttpServletRequest http, @RequestBody UserRequest request) {
        LogServiceRequest logModel = serviceLog.setRequestData(http, request);

        Response2<String> out;

        try {
            request.validateUserRegister();

            service.insert(request);

            out = new Response2<>(HttpStatus.CREATED, "¡Usuario registrado exitosamente!", true);

        } catch (InvalidFormException e) {
            out = new Response2<>(e);
        
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        serviceLog.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }



    @PutMapping(value = ApiConst.UPDATE_PASSWORD, produces = ApiConst.PRODUCES)
    public ResponseEntity<?> updatePassword(HttpServletRequest http, @RequestBody UserRequest request) { 
        LogServiceRequest logModel = serviceLog.setRequestData(http, request);

        Response2<String> out;

        try {
            String error;
            if ((error = request.validateUpdatePassword()) == null) {
                service.updatePassword(request);
                out = new Response2<>(HttpStatus.OK, "¡Contraseña actualizada exitosamente!", true);
            } else {
                out = new Response2<>(HttpStatus.BAD_REQUEST, error, false);
            }
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        }

        serviceLog.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }



    @PostMapping(value = ApiConst.EXISTING_USER, produces = ApiConst.PRODUCES)
    public ResponseEntity<?> existingUser(HttpServletRequest http,
            @RequestBody UserRequest request) {

        LogServiceRequest logModel = serviceLog.setRequestData(http, request);

        Response2<Boolean> out;

        try {
            request.validateUserRegister();

            Boolean existingUser = service.existingUser(request);

            if(existingUser)
                out = new Response2<>(HttpStatus.OK, "El usuario ingresado ya existe en el sistema", true, true);
            else
                out = new Response2<>(HttpStatus.OK, "El usuario ingresado no existe en el sistema", true, false);

        } catch (InvalidFormException e) {
            out = new Response2<>(e);
        
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        serviceLog.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

}
