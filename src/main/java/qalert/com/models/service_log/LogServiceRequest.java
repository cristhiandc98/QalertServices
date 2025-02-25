package qalert.com.models.service_log;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LogServiceRequest implements Serializable {

    private int logServiceId;

    private String requestCode;

    private Integer profileId;

    private String method;

    private String endPoint;

    private int httpStatusCode;

    private LocalDateTime beginDateTime;

    private LocalDateTime endDateTime;

    private String requestHeader;

    private String requestBody;

    private String responseBody;

    private String error;


    public LogServiceRequest() {
    }

    public int getLogServiceId() {
        return logServiceId;
    }

    public void setLogServiceId(int serviceLogId) {
        this.logServiceId = serviceLogId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer key_) {
        this.profileId = key_;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public LocalDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(LocalDateTime beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }



    public String getError() {
        return error;
    }



    public void setError(String error) {
        this.error = error;
    }



    public String getMethod() {
        return method;
    }



    public void setMethod(String method) {
        this.method = method;
    }



    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }



    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }




}
