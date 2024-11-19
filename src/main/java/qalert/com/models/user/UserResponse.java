package qalert.com.models.user;

import java.util.ArrayList;
import java.util.List;

import qalert.com.models.login.LoginResponse;
import qalert.com.models.person.PersonResponse;
import qalert.com.models.profile.ProfileResponse;

public class UserResponse extends PersonResponse{

    private Integer userId;
    
    private LoginResponse login = new LoginResponse();

    private List<ProfileResponse> profileList = new ArrayList<>();

    
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

    public List<ProfileResponse> getProfileList() {
        return profileList;
    }

    public void setProfileList(List<ProfileResponse> profileList) {
        this.profileList = profileList;
    }
    
}
