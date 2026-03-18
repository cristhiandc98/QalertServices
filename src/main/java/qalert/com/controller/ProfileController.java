package qalert.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.log.ILogService;
import qalert.com.interfaces.profile.IProfileService;
import qalert.com.models.generic.Response2;
import qalert.com.models.profile.ProfileRequest;
import qalert.com.models.profile.ProfileResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.ApiConst;
import qalert.com.utils.consts.UserMessageConst;

@RestController
@RequestMapping(ApiConst.PROFILE)
public class ProfileController {

    @Autowired
    private IProfileService service;

    @Autowired
    private ILogService logService;


    @PostMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> InsertProfile(HttpServletRequest http, @RequestBody ProfileRequest request) {
        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<?> out;

        try {
            if ((request.validateInsert(request.getUserId())) == null) {
                Long profileId = service.insert(request);
                out = new Response2<>(HttpStatus.CREATED, "Perfil insertado correctamente", true,profileId);
            } else {
                out = new Response2<>(HttpStatus.BAD_REQUEST);
            }
        } catch (DataAccessException ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

    @PutMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> UpdateProfile(HttpServletRequest http, @RequestBody ProfileRequest request) {
        LogServiceRequest logModel = logService.setRequestData(http, request);

        Response2<?> out;
        try {
            if ((request.validateInsert(request.getProfileId())) == null) 
                service.updateProfile(request);
                out = new Response2<>(HttpStatus.OK, "Perfil actualizado exitosamente.", true);
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

    @PatchMapping(produces = ApiConst.PRODUCES)
    public ResponseEntity<?> DeleteProfile(HttpServletRequest http, @RequestParam("profileId") Long profileId) {
        LogServiceRequest logModel = logService.setRequestData(http);

        Response2<?> out;

        try {
            if (profileId == null || profileId <= 0) {
                out = new Response2<>(HttpStatus.BAD_REQUEST, "Id de perfil inválido.", false);
            } else {
                service.deleteProfile(profileId);
                out = new Response2<>(HttpStatus.OK, "Perfil eliminado exitosamente", true);
            }
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);
        return ResponseEntity.status(out.getStatusCode()).body(out);
    }

    @GetMapping(value = ApiConst.LIST_PROFILES, produces = ApiConst.PRODUCES)
    public ResponseEntity<?> ListProfiles(HttpServletRequest http, @RequestParam("userId") Long userId) {
        LogServiceRequest logModel = logService.setRequestData(http);

        Response2<List<ProfileResponse>> out;
        try {
            if (userId != null) {
                out = new Response2<>(service.listProfiles(userId));
            } else {
                out = new Response2<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST, false);
            }
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        logService.setResponseDataAndSave(logModel, out);

        return ResponseEntity.status(out.getStatusCode()).body(out);

    }

}
