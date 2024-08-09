package qalert.com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IUser;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserResponse;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Qualifier(qalert.com.utils.consts.Consts.QALIFIER_SERVICE)
	@Autowired
	private IUser userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try {
			LoginRequest loginRequest = new LoginRequest(username);
			loginRequest.separateUserNameAndDeviceId();
			
			UserResponse userResponse = userService.login(loginRequest).getData();
			
			if(userResponse == null)
				throw new UsernameNotFoundException(username);
				
			return new UserDetailsImpl(userResponse);
			
		} catch (Exception e) {
			throw new UsernameNotFoundException(username);
		}
	}

}
