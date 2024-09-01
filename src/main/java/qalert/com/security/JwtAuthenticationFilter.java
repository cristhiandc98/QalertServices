package qalert.com.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.login.TokenResponse;
import qalert.com.models.user.UserResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LoginRequest login = null;

		try {
			login = new ObjectMapper().readValue(request.getReader(), LoginRequest.class);
			if(login.validateLogin())
				login.joinUserNameAndDeviceId();
			else 			
				login = new LoginRequest();
		} catch (Exception ex) {
			login = new LoginRequest();
            logger.error(new Response_<>(ex, request).getErrorMssg());
		}
		
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
			login.getUserName(), login.getPassword(), Collections.emptyList());
		
		return getAuthenticationManager().authenticate(upat);
	}
	
	@Override
	public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filter
			,Authentication authentication) throws IOException, ServletException {
		
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
		response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(new Response_<UserResponse>(udi.getUser())));
		response.getWriter().flush();
		super.successfulAuthentication(request, response, filter, authentication);
	}
	
}
