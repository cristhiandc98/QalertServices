package qalert.com.interfaces;

import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;

public interface  ILogService {

    LogServiceRequest setRequestData(HttpServletRequest httpRequest, Object request, Integer profileId, boolean isPrivate);
    
    <T> void hideRequestPrivateDataHide(LogServiceRequest logModel, T request);

    <T> void setResponseData(LogServiceRequest logModel, Response2<T> response, boolean isPrivate);

    <T> void hideResponsePrivateDataHide(LogServiceRequest logModel, Response2<T> request);

    void insert(LogServiceRequest request);

    <T> CompletableFuture<Void> save(LogServiceRequest logModel);

    <T> T clone(T request, Class<T> clazz);

    <T> void setResponseDataAndSave(LogServiceRequest logModel, Response2<T> response, boolean isPrivate);
}
