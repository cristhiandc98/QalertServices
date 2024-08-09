package qalert.com.models.login;

public class LoginRequest {

    private String userName;

    private String password;

    private Integer deviceId;

    private String verificationCode;

    //***************************************************************
    //********************************************************METHODS
    //***************************************************************
    public void joinUserNameAndDeviceId(){
        userName = userName + ":" + password;
    }

    public void separateUserNameAndDeviceId(){
        String[] x = userName.split(":");
        userName = x[0];
        password = x[1];
    }


    //***************************************************************
    //***************************************************CONSTRUCTORS
    //***************************************************************    
    public LoginRequest(String userName, String password, Integer deviceId) {
        this.userName = userName;
        this.password = password;
        this.deviceId = deviceId;
    }

    public LoginRequest(String userName) {
        this.userName = userName;
    }

    public LoginRequest() {
    }

    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String codigoVerificacion) {
        this.verificationCode = codigoVerificacion;
    }
}
