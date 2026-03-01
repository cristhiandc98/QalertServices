package qalert.com.utils.exceptions;

public class ConflictException extends RuntimeException {

    public String errorMssg;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, String errorMssg) {
        super(message);
        this.errorMssg = errorMssg;
    }
}
