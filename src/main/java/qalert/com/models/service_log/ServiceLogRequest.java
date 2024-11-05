package qalert.com.models.service_log;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ServiceLogRequest implements Serializable {

    private int serviceLogId;

    private String requestCode;

    private Integer key_;

    private Integer keyType;

    private String endPoint;

    private int httpStatusCode;

    private LocalDateTime beginDateTime;

    private String requestHeader;

    private String requestBody;

    private String responseBody;

    private String error;

    public ServiceLogRequest() {
    }



    public int getServiceLogId() {
        return serviceLogId;
    }

    public void setServiceLogId(int serviceLogId) {
        this.serviceLogId = serviceLogId;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public Integer getKey_() {
        return key_;
    }

    public void setKey_(Integer key_) {
        this.key_ = key_;
    }

    public Integer getKeyType() {
        return keyType;
    }

    public void setKeyType(Integer keyType) {
        this.keyType = keyType;
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




}
