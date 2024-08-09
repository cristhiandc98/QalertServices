package qalert.com.models.login;

public class TokenResponse {

    private String token;

    private String expireAt;

    
    public TokenResponse(String token, String expireAt) {
        this.token = token;
        this.expireAt = expireAt;
    }

    public TokenResponse() {
    }

    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }
}
