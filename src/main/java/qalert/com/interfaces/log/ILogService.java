package qalert.com.interfaces.log;

import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;

public interface  ILogService {

    LogServiceRequest setRequestData(HttpServletRequest httpRequest);
    LogServiceRequest setRequestData(HttpServletRequest httpRequest, Object request);

    <T> void setResponseData(LogServiceRequest logModel, Response2<T> response);

    <T> CompletableFuture<Void> save(LogServiceRequest logModel);

    <T> void setResponseDataAndSave(LogServiceRequest logModel, Response2<T> response);
}
