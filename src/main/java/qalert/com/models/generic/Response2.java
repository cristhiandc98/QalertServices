package qalert.com.models.generic;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.exceptions.ConflictException;
import qalert.com.utils.exceptions.InvalidFormException;

public class Response2<T> {

    private boolean status;

    private String userMssg;

    private T data;

    @JsonIgnore
    private HttpStatus statusCode;

    @JsonIgnore
    private String errorMssg;

    // ***************************************************************
    // ***************************************************CONSTRUCTORS
    // ***************************************************************
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

    public Response2(T data) {
        this.statusCode = HttpStatus.OK;
        this.userMssg = UserMessageConst.SUCCESS;
        this.data = data;
        this.status = data != null;
    }

    public <Y> Response2(Response2<Y> in) {
        status = in.isStatus();
        userMssg = in.getUserMssg();
        statusCode = in.getStatusCode();
        errorMssg = in.getErrorMssg();
    }

    public Response2(HttpStatus statusCode, String userMssg, boolean status, T data) {
        this.statusCode = statusCode;
        this.userMssg = userMssg;
        this.status = status;
        this.data = data;
    }

    // ***********************************************************************
    // ************************************************************ Exception
    // ***********************************************************************
    public Response2(InvalidFormException exception) {
        this(HttpStatus.BAD_REQUEST, exception.getMessage(), false);
        setError(exception);
    }

    public Response2(ConflictException exception) {
        this(HttpStatus.CONFLICT, exception.getMessage(), false);

        if(exception.errorMssg == null)
            setError(exception);
        else 
            errorMssg = exception.errorMssg;
    }

    public Response2(DataAccessException ex) {

        SQLException sqlEx = (SQLException) ex.getMostSpecificCause();

        userMssg = sqlEx.getMessage();

        switch (sqlEx.getErrorCode()) {
            case 50001:
                statusCode = HttpStatus.CONFLICT;
                break;
            default:
                userMssg = UserMessageConst.INTERNAL_SERVER_ERROR;
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
                setError(ex);
                break;
        }
    }

    public Response2(Exception exception) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, UserMessageConst.INTERNAL_SERVER_ERROR, false);
        setError(exception);
    }

    public Response2(Exception exception, String userMssg) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, userMssg, false);
        setError(exception);
    }

    public String setError(Exception exception) {
        try {
            StackTraceElement elemento = exception.getStackTrace()[0];

            errorMssg += " | class: " + elemento.getClassName() +
                    " | line: " + elemento.getLineNumber() +
                    " | method: " + elemento.getMethodName() +
                    " | error: " + exception.getMessage();
        } catch (Exception e) {
            errorMssg += " | json: -";
        }

        return errorMssg;
    }

    // ***************************************************************
    // *********************************************GETTERS AND SETTER
    // ***************************************************************
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
