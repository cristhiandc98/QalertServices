package qalert.com.models.generic;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.utils.DateUtil;

public class Response2<T> {

    private boolean status;    

    private String userMssg;

    private T data;

    private String errorId;

    private HttpStatus statusCode;

    @JsonIgnore
    private String errorMssg;


    //***************************************************************
    //***************************************************CONSTRUCTORS
    //***************************************************************
    public Response2() {
        status = true;
        this.userMssg = UserMessageConst.SUCCESS;
        statusCode = HttpStatus.OK;
    }
    
    public Response2(HttpStatus statusCode, String userMssg, boolean status) {
        this.statusCode = statusCode;
        this.userMssg = userMssg;
        this.status = status;
    }

    //Exception
    public Response2(Exception exception) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, UserMessageConst.INTERNAL_SERVER_ERROR, false);
        setError(exception);
    } 
    
    public Response2(Exception exception, String userMssg) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, userMssg, false);
        setError(exception);
    }   

    public String setError(Exception exception)
    {
        errorId = DateUtil.generateId();
        
        errorMssg = " codError: " + errorId;

        try {
            StackTraceElement elemento = exception.getStackTrace()[0];

                errorMssg += " | class: " + elemento.getClassName() +
                " | line: " + elemento.getLineNumber() +
                " | method: " + elemento.getMethodName() +
                " | error: "+ exception.getMessage();
        } catch (Exception e) {
            errorMssg += " | json: -";
        }

        return errorMssg;
    }

    //***************************************************************
    public Response2(T data, String userMessage) {
        this.data = data;
        this.status = data != null;
        this.userMssg = userMessage;
        this.statusCode = HttpStatus.OK;
    }

    public Response2(T data) {
        this.statusCode = HttpStatus.OK;
        this.userMssg = UserMessageConst.SUCCESS;
        this.data = data;
        this.status = data != null;
    }

    public Response2(HttpStatus statusCode, String userMssg) {
        this.statusCode = statusCode;
        this.userMssg = userMssg;
        this.status = statusCode == HttpStatus.OK;
    }
    
    //Exception
    public Response2(Exception exception, Object object) {
        errorId = DateUtil.generateId();
        status = false;
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        userMssg = UserMessageConst.INTERNAL_SERVER_ERROR;
        errorMssg = " | error: " + exception.getLocalizedMessage();
    }    

    public <Y>Response2(Response2<Y> in){
        status = in.isStatus();
        userMssg = in.getUserMssg();
        errorId = in.getErrorId();
        statusCode = in.getStatusCode();
        errorMssg = in.getErrorMssg();
    }

    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserMssg() {
        return userMssg;
    }

    public void setUserMssg(String userMssg) {
        this.userMssg = userMssg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorId() {
        return errorId;
    }

    public void setErrorId(String errorId) {
        this.errorId = errorId;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMssg() {
        return errorMssg;
    }

    public void setErrorMssg(String errorMssg) {
        this.errorMssg = errorMssg;
    }

}
