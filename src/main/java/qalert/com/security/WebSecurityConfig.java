package qalert.com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import qalert.com.utils.consts.Url;
	
@Configuration
public class WebSecurityConfig {
    
	@Autowired
	private UserDetailsService userDetailsService;	

	@Autowired
	private JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}
	
    @Autowired
    private PasswordEncoder encryptador;
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager auth, MvcRequestMatcher.Builder mvc) throws Exception {
		JwtAuthenticationFilter jwtAuthentication = new JwtAuthenticationFilter();
		jwtAuthentication.setAuthenticationManager(auth);
		jwtAuthentication.setFilterProcessesUrl(Url.SECURITY + Url.LOGIN);
		
		return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> {
					//requests.requestMatchers("/**").permitAll();
					requests.requestMatchers(mvc.pattern(Url.SECURITY + Url.VALIDATE_EMAIL)).permitAll();
					requests.requestMatchers(mvc.pattern(Url.SECURITY + Url.CREATE_USER)).permitAll();
					requests.requestMatchers(mvc.pattern(Url.SECURITY + Url.RESET_DEVICE_ID)).permitAll();
					requests.requestMatchers(mvc.pattern(Url.SECURITY + "/")).permitAll();
					requests.anyRequest().authenticated();
				})
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtAuthentication)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
	}
	
	@Bean
	AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(encryptador)
				.and()
				.build();
	}	
}
