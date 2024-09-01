package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.Consts;
import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.utils.DateUtil;

@Qualifier(Consts.QALIFIER_SERVICE)
@Service
public class UserServiceImpl implements IUser{

    @Qualifier(Consts.QALIFIER_DAO)
    @Autowired
    private IUser dao;

    @Autowired
    private PasswordEncoder encryptador;

    @Override
    public Response_<String> insert(UserRequest request) {
        request.getLogin().setDeviceId(Integer.parseInt(request.getLogin().getVerificationCode()));
        request.getLogin().setPassword(encryptador.encode(request.getLogin().getPassword()));
        return dao.insert(request);
    }

    @Override
    public Response_<UserResponse> login(LoginRequest request) {
		return dao.login(request);
    }

}
