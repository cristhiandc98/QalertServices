package qalert.com.services;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
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

    private LogServiceRequest serviceLogModel;

    @Override
    public void insert(LogServiceRequest request) {
        logDao.insert(request);
    }

    @Override
    public <T> CompletableFuture<Void> save(Response_<T> response)
    {
        return CompletableFuture.runAsync(() -> {
            setResponseData(response);
    
            insert(serviceLogModel);
        });
    }

    @Override
    public <T> void savePrivate(Response_<T> response) {
        if (response.getData() != null){
            if (response.getData() instanceof UserResponse user){
                String token = user.getLogin().getToken().getToken();
                user.getLogin().getToken().setToken(null);
                save(response);
                //user.getLogin().getToken().setToken(token);
            }
        }
        else save(response);
    }

    @Override
    public void setRequestData(HttpServletRequest httpRequest, Object request)
    {
        serviceLogModel = new LogServiceRequest();
        serviceLogModel.setBeginDateTime(LocalDateTime.now());
        serviceLogModel.setMethod(httpRequest.getMethod());
        serviceLogModel.setEndPoint(httpRequest.getRequestURI());

        serviceLogModel.setProfileId(httpRequest.getHeader("profile_id") == null ? null : Integer.valueOf(httpRequest.getHeader("profile_id")));
        
        try
        {
            serviceLogModel.setRequestBody(request == null ? null : objectMapper.writeValueAsString(request));
        }
        catch (JsonProcessingException ex)
        {
            serviceLogModel.setRequestBody(ex.getMessage());
        }
    }

    @Override
    public void setRequestPrivateData(HttpServletRequest httpRequest, Object request) {
        if (request != null){
            if (request instanceof LoginRequest login){
                String password = login.getPassword();
                login.setPassword(null);
                setRequestData(httpRequest, request);
                login.setPassword(password);
            }
        } 
        else setRequestData(httpRequest, request);
    }

    private <T> void setResponseData(Response_<T> response)
    {
        serviceLogModel.setRequestCode(response.getErrorId() == null ? DateUtil.generateId() : response.getErrorId());
        serviceLogModel.setError(response.getErrorMssg());
        serviceLogModel.setHttpStatusCode(response.getStatusCode().value());

        try
        {
            serviceLogModel.setResponseBody(objectMapper.writeValueAsString(response));
        }
        catch (JsonProcessingException ex)
        {
            serviceLogModel.setResponseBody(ex.getMessage());
        }
    }
}
