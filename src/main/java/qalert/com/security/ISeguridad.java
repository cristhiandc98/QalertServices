package qalert.com.security;

import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;

public interface ISeguridad {

    Response_<String> saveVerificationCode(UserRequest request, boolean isChangeDevice);

    Response_<String> resetIdDevice(UserRequest request);
    
}
