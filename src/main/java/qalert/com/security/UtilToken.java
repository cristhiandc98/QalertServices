package qalert.com.security;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.login.TokenResponse;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.utils.DateUtil;

public class UtilToken {
    
    private static final String ACCES_TOKEN_SECRET = "$2a$10$ZTBFEpVID1vf5Luffx1mbeFedEHhaBsEJcC20t1kHLOt5BPTqOS9C"; //new BCryptPasswordEncoder().encode("M0QS9APGC+1FGG*/3DIGUV8BYB7"); Se utilizó esta cadena para encryptar y pasarla como clave para generar el token
	private static final Long TIME_EXPIRATION = 600L * 1000;
	
	public static void createToken(UserResponse user) {
		Date expirationDate = new Date(System.currentTimeMillis() + TIME_EXPIRATION);
		
		Map<String, Object> extra = new HashMap<>();
		extra.put(CommonConsts.KEY_USER_ID, user.getUserId());
		
		//Generate token
		String tokenString = Jwts.builder()
				.setSubject(user.getLogin().getUserName())
				.setExpiration(expirationDate)
				.addClaims(extra) 
				.signWith(Keys.hmacShaKeyFor(ACCES_TOKEN_SECRET.getBytes()))
				.compact();			
				
		//Set Token
    LoginResponse login = user.getLogin();
    if (login == null) { // por si acaso viene null
        login = new LoginResponse();
        user.setLogin(login);
    }

    TokenResponse token = new TokenResponse(tokenString, DateUtil.getStringDateTimeFromDate(expirationDate));
    login.setToken(token);
	}
	
	public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(ACCES_TOKEN_SECRET.getBytes())
					.build()
					.parseClaimsJws(token)
					.getBody();
			long userId = Long.parseLong(claims.get(CommonConsts.KEY_USER_ID).toString());
			
			return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
		} catch (JwtException e) {
			return null;
		}
	}
}
