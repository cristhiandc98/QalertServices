package qalert.com.models.generic;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.utils.DateUtil;

public class Response_<T> {

    private boolean status;    

    private String userMssg;

    private T data;

    private String errorId;

    private HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    @JsonIgnore
    private String errorMssg;


    //***************************************************************
    //***************************************************CONSTRUCTORS
    //***************************************************************
    public Response_() {
    }
    
    public Response_(T data) {
        this.data = data;
        this.userMssg = UserMessageConst.SUCCESS;
        this.status = true;
    }

    public Response_(HttpStatus statusCode, String userMssg) {
        this.statusCode = statusCode;
        this.userMssg = userMssg;
        this.status = statusCode == HttpStatus.OK;
        this.errorId = DateUtil.generateId();
    }
    
    public Response_(HttpStatus statusCode, String userMssg, boolean status) {
        this(statusCode, userMssg);
        this.status = status;
    }
    
    //Exception
    public Response_(Exception exception, Object object) {
        errorId = DateUtil.generateId();
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        userMssg = UserMessageConst.INTERNAL_SERVER_ERROR;
        errorMssg = exception.getMessage();
        try {
            errorMssg += new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            errorMssg += "Error al deserializar";
        }
    }   
    
    //Exception
    public Response_(Exception exception, Object object, String userMssg) {
        this(exception, object);
        this.userMssg = userMssg;
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
