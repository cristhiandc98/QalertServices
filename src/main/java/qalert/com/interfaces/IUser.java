package qalert.com.interfaces;

import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;

public interface IUser {

    Response_<String> insert(UserRequest request);

    Response_<String> updatePassword(UserRequest request);
	
    Response_<UserResponse> login(LoginRequest request);

}
