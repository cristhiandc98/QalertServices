package qalert.com.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import qalert.com.models.login.LoginResponse;
import qalert.com.models.user.UserResponse;

public class UserDetailsImpl implements UserDetails{

	private static final long serialVersionUID = 1L;
	private final UserResponse user;

	public UserDetailsImpl(UserResponse user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getLogin().getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getLogin().getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public UserResponse getUser() {
		return user;
	}

}
