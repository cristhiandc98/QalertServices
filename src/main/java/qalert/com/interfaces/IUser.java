package qalert.com.interfaces;

import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;

public interface IUser {

    void insert(UserRequest request);

    void updatePassword(UserRequest request);
	
    Response2<UserResponse> login(LoginRequest request);

    Response2<String> validateNewUser(UserRequest request);

}
