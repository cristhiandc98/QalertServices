package qalert.com.interfaces;

import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.models.generic.Response_;
import qalert.com.models.service_log.LogServiceRequest;

public interface  ILogService {

    void setRequestData(HttpServletRequest httpRequest, Object request);

    void setRequestPrivateData(HttpServletRequest httpRequest, Object request);

    void insert(LogServiceRequest request);

    <T> CompletableFuture<Void> save(Response_<T> response);

    <T> void savePrivate(Response_<T> response);
}
