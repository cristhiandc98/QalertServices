package qalert.com.models.login;

public class LoginResponse extends LoginRequest{
    
    private TokenResponse token;


    public LoginResponse(){
    }

    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public TokenResponse getToken() {
        return token;
    }

    public void setToken(TokenResponse token) {
        this.token = token;
    }

}
