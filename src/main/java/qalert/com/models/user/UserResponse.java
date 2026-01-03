package qalert.com.models.user;

import java.util.ArrayList;
import java.util.List;

import qalert.com.models.login.LoginResponse;
import qalert.com.models.person.PersonResponse;
import qalert.com.models.profile.ProfileResponse;

public class UserResponse extends PersonResponse {

    private Long userId;

    private LoginResponse login = new LoginResponse();

    private Integer subscriptionId;

    private List<ProfileResponse> profileList = new ArrayList<>();

    // ***************************************************************
    // *********************************************GETTERS AND SETTER
    // ***************************************************************
    public LoginResponse getLogin() {
        return login;
    }

    public void setLogin(LoginResponse login) {
        this.login = login;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ProfileResponse> getProfileList() {
        return profileList;
    }

    public void setProfileList(List<ProfileResponse> profileList) {
        this.profileList = profileList;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

}
