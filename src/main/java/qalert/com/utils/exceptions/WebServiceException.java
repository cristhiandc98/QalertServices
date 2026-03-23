package qalert.com.utils.exceptions;

public class WebServiceException extends RuntimeException {

    private Object request;
    private Object response;

    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Object request, Object response) {
        super(message);
        this.request = request;
        this.response = response;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
