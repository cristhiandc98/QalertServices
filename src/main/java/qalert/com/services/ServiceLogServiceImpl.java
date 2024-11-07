package qalert.com.services;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.service_log.LogServiceRequest;
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
            if (response.getData().getClass() == LoginResponse.class){
                LoginResponse loginResponse = (LoginResponse)response.getData();
                String token = loginResponse.getToken().getToken();
                loginResponse.getToken().setToken(null);
                save(response);
                loginResponse.getToken().setToken(token);
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
        serviceLogModel.setRequestHeader(httpRequest.getHeader("null"));

        try
        {
            serviceLogModel.setRequestBody(request == null ? null : objectMapper.writeValueAsString(request));

            //serviceLogModel.Llave = context.Request.Headers[CommonConst.HEADER_REFERENCE_KEY].ToString().Trim();
        }
        catch (Exception ex)
        {
            serviceLogModel.setRequestBody(ex.getMessage());
        }
    }

    private <T> void setResponseData(Response_<T> response)
    {
        serviceLogModel.setRequestCode(response.getErrorId() == null ? DateUtil.generateId() : response.getErrorId());
        serviceLogModel.setError(response.getErrorMssg());
        serviceLogModel.setHttpStatusCode(response.getStatusCode().value());

        try
        {
            serviceLogModel.setResponseBody(objectMapper.writeValueAsString(response));

            // if(response!.StatusCode == HttpStatusCode.InternalServerError) 
            //     _ = SendEmailApiClient.Simple(appSettings.Value, GenerateEmailRequest(response.CodRequest!));
        }
        catch (Exception ex)
        {
            serviceLogModel.setResponseBody(ex.getMessage());
        }
    }


    //     private SendEmailRequest GenerateEmailRequest(string idError)
    //     {
    //         SendEmailRequest out_ = new SendEmailRequest();

    //         out_.Destinatario = appSettings.Value.Notifications!.Error_!.Addressees;
    //         out_.Asunto = appSettings.Value.Notifications!.Error_!.Subject;
    //         out_.MensajeHtml = "Código de la solicitud: " + idError;

    //         return out_;
    //     }

}
