package qalert.com.models.user;

import qalert.com.models.login.LoginResponse;
import qalert.com.models.person.PersonResponse;

public class UserResponse extends PersonResponse{

    private Integer userId;
    
    private LoginResponse login;

    
    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public LoginResponse getLogin() {
        return login;
    }

    public void setLogin(LoginResponse login) {
        this.login = login;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
}
