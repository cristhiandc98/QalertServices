package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class UserServiceImpl implements IUser{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private IUser dao;

    @Autowired
    private PasswordEncoder encryptador;

    @Override
    public Response_<String> insert(UserRequest request) {
        request.getLogin().setDeviceId(Integer.parseInt(request.getLogin().getVerificationCode()));
        request.getLogin().setPassword(encryptador.encode(request.getLogin().getPassword()));
        
        Response_<String> out = dao.insert(request);

        if(out.isStatus())
          out.setData(request.getLogin().getVerificationCode());

        return out;
    }

    @Override
    public Response_<UserResponse> login(LoginRequest request) {
		  return dao.login(request);
    }

    @Override
    public Response_<String> updatePassword(UserRequest request) {
      request.getLogin().setPassword(encryptador.encode(request.getLogin().getPassword()));
		  Response_<String> out = dao.updatePassword(request);

        if( out.isStatus())
          out.setData(request.getLogin().getVerificationCode());

        return out;
    }

}
