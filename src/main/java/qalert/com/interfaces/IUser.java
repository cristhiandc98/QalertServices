package qalert.com.interfaces;

import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;

public interface IUser {

    Response2<String> insert(UserRequest request);

    Response2<String> updatePassword(UserRequest request);
	
    Response2<UserResponse> login(LoginRequest request);

}
