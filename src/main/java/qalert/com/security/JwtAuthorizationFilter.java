package qalert.com.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String bearerToken = request.getHeader(Consts.AUTHORIZATION);
			
		if(bearerToken != null && bearerToken.startsWith(Consts.BEARER)) {
			String token = bearerToken.replace(Consts.BEARER, "");
			UsernamePasswordAuthenticationToken upat = UtilToken.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(upat);
		}
		
		filterChain.doFilter(request, response);
	}
	
}
