package qalert.com.services;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.log.ILogDao;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.EnvironmentConst;
import qalert.com.utils.utils.DateUtil;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class ServiceLogServiceImpl implements ILogService {

    @Autowired
    private ILogDao logDao;

    @Autowired
    private ObjectMapper objectMapper;

	@Autowired
	private Environment env;


    @Override
    public LogServiceRequest setRequestData(HttpServletRequest httpRequest){
        return setRequestData(httpRequest,null);
    } 



    @Override
    public LogServiceRequest setRequestData(HttpServletRequest httpRequest, Object request) {
        LogServiceRequest logModel = new LogServiceRequest();

        logModel.setEndPoint(httpRequest.getRequestURI().replaceAll(env.getProperty(EnvironmentConst.SERVICE_CONTEXT), ""));
        logModel.setMethod(httpRequest.getMethod());
        logModel.setBeginDateTime(DateUtil.getCurrentDateTime());

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated())
                if (auth.getPrincipal() instanceof Long id)
                    logModel.setUserId(id);


            logModel.setRequestBody(request == null ? null : objectMapper.writeValueAsString(request));

            httpRequest.getParameterMap().forEach((key, value) -> {
                logModel.setRequestHeader(key + " = " + Arrays.toString(value));
            });


            String profileIdString = httpRequest.getHeader(CommonConsts.KEY_PROFILE_ID);
            if (profileIdString != null && !profileIdString.isEmpty()) {
                logModel.setProfileId(Long.parseLong(profileIdString));
            }
        } catch (JsonProcessingException ex) {
            logModel.setRequestBody(ex.getMessage());
        }

        return logModel;
    }

    

    public <T> void setResponseData(LogServiceRequest logModel, Response2<T> response) {
        logModel.setHttpStatusCode(response.getStatusCode().value());
        logModel.setEndDateTime(DateUtil.getCurrentDateTime());

        try {
            logModel.setResponseBody(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException ex) {
            logModel.setError(ex.getMessage());
        }

        logModel.setError(response.getErrorMssg());
    }



    @Override
    public <T> CompletableFuture<Void> save(LogServiceRequest logModel) {
        return CompletableFuture.runAsync(() -> {
            logDao.insert(logModel);
        });
    }



    @Override
    public <T> void setResponseDataAndSave(LogServiceRequest logModel, Response2<T> response) {
        setResponseData(logModel, response);

        save(logModel);
    }
}
