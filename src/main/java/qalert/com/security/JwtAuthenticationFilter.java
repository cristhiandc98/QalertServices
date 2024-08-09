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

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private static final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LoginRequest user = null;

		try {
			user = new ObjectMapper().readValue(request.getReader(), LoginRequest.class);
			user.joinUserNameAndDeviceId();
		} catch (Exception ex) {
			user = new LoginRequest();

            logger.error(new Response_<>(ex, request).getErrorMssg());
		}
		
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(
				user.getUserName(), user.getPassword(), Collections.emptyList());
		
		return getAuthenticationManager().authenticate(upat);
	}
	
	@Override
	public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filter
			,Authentication authentication) throws IOException, ServletException {
		
		//Recuperate claims data
		UserDetailsImpl udi = (UserDetailsImpl) authentication.getPrincipal();
		LoginResponse loginResponse = udi.getUser(); //UtilToken.crearToken());
			
		//Whiten login data
		loginResponse.setPassword(null);
		loginResponse.setUserName(null);
		
		//Set token
		TokenResponse tokenResponse = new TokenResponse();
		tokenResponse.setToken(Consts.BEARER + loginResponse.getToken());
		loginResponse.setToken(tokenResponse);
		
		//Return
		response.getWriter().write(new ObjectMapper().writeValueAsString(loginResponse));
		response.getWriter().flush();
		super.successfulAuthentication(request, response, filter, authentication);
	}
	
}
