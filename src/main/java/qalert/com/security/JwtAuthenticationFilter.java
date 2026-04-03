package qalert.com.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import qalert.com.interfaces.log.ILogService;
import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.login.TokenResponse;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.UserMessageConst;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private ILogService serviceLog;

    private LogServiceRequest logModel;

    public JwtAuthenticationFilter(ILogService serviceLog) {
        this.serviceLog = serviceLog;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        LoginRequest login;

        try {
            login = new ObjectMapper().readValue(request.getReader(), LoginRequest.class);

            String pass = login.getPassword();
            login.setPassword(null);
            logModel = serviceLog.setRequestData(request, login);
            login.setPassword(pass);

            if (login.validateLogin() == null) {
                login.joinUserNameAndDeviceId();
            } else {
                login = new LoginRequest();
            }
        } catch (Exception ex) {
            login = new LoginRequest();
        }

        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
                login.getUserName(), login.getPassword(), Collections.emptyList());

        return getAuthenticationManager().authenticate(upat);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(CommonConsts.CONTENT_TYPE);
        response.setCharacterEncoding(CommonConsts.ENCODING);

        //Return
        Response2<UserResponse> out = new Response2<UserResponse>(HttpStatus.UNAUTHORIZED, UserMessageConst.UNAUTHORIZED, false);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(CommonConsts.CONTENT_TYPE);
        response.setCharacterEncoding(CommonConsts.ENCODING);
        response.getWriter().write(new ObjectMapper().writeValueAsString(out));
        response.getWriter().flush();
        out.setErrorMssg(failed.getMessage());

        serviceLog.setResponseDataAndSave(logModel, out);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filter,
            Authentication authentication) throws IOException, ServletException {

		//Recuperate claims data
		UserDetailsImpl udi = (UserDetailsImpl) authentication.getPrincipal();

		
		UtilToken.createToken(udi.getUser());
		//Whiten login data
		LoginResponse login = udi.getUser().getLogin();
		login.setPassword(null);
		login.setUserName(null);
		
		//Set token
		TokenResponse tokenResponse = new TokenResponse();
		tokenResponse.setToken(Consts.BEARER + login.getToken().getToken());
		login.setToken(tokenResponse);
		
		//Return
		Response2<UserResponse> out = new Response2<UserResponse>(udi.getUser());
		response.setContentType(CommonConsts.CONTENT_TYPE);
    	response.setCharacterEncoding(CommonConsts.ENCODING);
		response.getWriter().write(new ObjectMapper().writeValueAsString(out));
		response.getWriter().flush();

		super.successfulAuthentication(request, response, filter, authentication);

        String token = out.getData().getLogin().getToken().getToken();
        out.getData().getLogin().getToken().setToken(null);
        logModel.setUserId(udi.getUser().getUserId());
		serviceLog.setResponseDataAndSave(logModel, out);
        out.getData().getLogin().getToken().setToken(token);
    }
}
