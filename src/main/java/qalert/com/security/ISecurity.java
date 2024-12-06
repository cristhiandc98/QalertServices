package qalert.com.security;

import qalert.com.models.generic.Response2;
import qalert.com.models.user.UserRequest;

public interface ISecurity {

    Response2<String> saveVerificationCode(UserRequest request, boolean isChangeDevice);

    Response2<String> resetIdDevice(UserRequest request);
    
}
