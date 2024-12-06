package qalert.com.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.master.MasterResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.utils.DateUtil;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class ServiceLogServiceImpl implements ILogService{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private ILogService logDao;

	@Autowired
	private ObjectMapper objectMapper;

    @Override
    public LogServiceRequest setRequestData(HttpServletRequest httpRequest, Object request, Integer profileId, boolean isPrivate)
    {
        LogServiceRequest logModel = new LogServiceRequest();
        
        logModel.setMethod(httpRequest.getMethod());
        logModel.setEndPoint(httpRequest.getRequestURI());
        logModel.setBeginDateTime(DateUtil.getCurrentDateTime());
        logModel.setProfileId(profileId);
        logModel.setResponseBody("");
        
        try
        {
            logModel.setRequestBody(request == null ? null : objectMapper.writeValueAsString(request));

            if(profileId == null){
                String profileIdString = httpRequest.getHeader("profile_id");
                if(profileIdString != null && !profileIdString.isEmpty())
                logModel.setProfileId(Integer.parseInt(profileIdString));
            }
            
            if(isPrivate)
                hideRequestPrivateDataHide(logModel, request);
        }
        catch (JsonProcessingException ex)
        {
            logModel.setRequestBody(ex.getMessage());
        }

        return logModel;
    }


    @Override
    public <T> void hideRequestPrivateDataHide(LogServiceRequest logModel, T request) {
        
        if(request == null) return;

        try {
            if (request instanceof UserResponse user){
                UserResponse data = clone(user, UserResponse.class);

                data.getLogin().getToken().setToken(null);
                
                logModel.setRequestBody(objectMapper.writeValueAsString(data));
            }
        } catch (Exception e) {
            logModel.setRequestCode(e.getMessage());
        }
    }


    public <T> void setResponseData(LogServiceRequest logModel, Response2<T> response, boolean isPrivate)
    {
        logModel.setRequestCode(response.getErrorId() == null ? DateUtil.generateId() : response.getErrorId());
        logModel.setError(response.getErrorMssg());
        logModel.setHttpStatusCode(response.getStatusCode().value());
        logModel.setEndDateTime(DateUtil.getCurrentDateTime());

        try
        {
            if(response != null && !isPrivate)
                logModel.setResponseBody(objectMapper.writeValueAsString(response) + logModel.getResponseBody());
        }
        catch (JsonProcessingException ex)
        {
            logModel.setResponseBody(ex.getMessage());
        }

        logModel.setError(response.getErrorMssg());
    }


    public <T> void hideResponsePrivateDataHide(LogServiceRequest logModel, Response2<T> response){
        
        if (response.getData() == null) return;

        try {
            if (response.getData() instanceof LoginResponse login){

                Response2<LoginResponse> clon = new Response2<>(response);

                LoginResponse dataClon = clone(login, LoginResponse.class);
                dataClon.getToken().setToken(null);

                clon.setData(dataClon);

                logModel.setResponseBody(objectMapper.writeValueAsString(clon));
            
            }else if(response.getData() instanceof MasterResponse master){

                if(master.getTableId() == 1 && master.getFieldId() == 1){

                    Response2<MasterResponse> clon = new Response2<>(response);
    
                    MasterResponse dataClon = clone(master, MasterResponse.class);
                    
                    dataClon.setValueVarchar("This field is too big.");
    
                    clon.setData(dataClon);

                    logModel.setResponseBody(objectMapper.writeValueAsString(clon));
                }
            }
        } catch (Exception e) {
            logModel.setResponseBody(e.getMessage());
        }
    }

    @Override
    public void insert(LogServiceRequest request) {
        logDao.insert(request);
    }

    @Override
    public <T> CompletableFuture<Void> save(LogServiceRequest logModel)
    {
        return CompletableFuture.runAsync(() -> {    
            insert(logModel);
        });
    }

    @Override
    public <T> T clone(T request, Class<T> clazz) {
        try {
            // Convertir el objeto a JSON
            String json = objectMapper.writeValueAsString(request);

            // Convertir el JSON de vuelta al objeto
            T responseClon = objectMapper.readValue(json, clazz);

            return responseClon;
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public <T> void setResponseDataAndSave(LogServiceRequest logModel, Response2<T> response, boolean isPrivate) {
        setResponseData(logModel, response, isPrivate);

        if(isPrivate)
            hideResponsePrivateDataHide(logModel, response);

        save(logModel);
    }
}
