package qalert.com.security;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import qalert.com.models.generic.Response_;
import qalert.com.utils.consts.UserMessageConst;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
 
    @ExceptionHandler(Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	public Response_<String> handleUnauthorizedException(RuntimeException ex) {
        return new Response_<>(HttpStatus.UNAUTHORIZED, UserMessageConst.UNAUTHORIZED);
    }
}
