package qalert.com.models.login;

import java.util.regex.Pattern;

import qalert.com.utils.utils.RegexUtil;

public class LoginRequest {

    private String userName;

    private String password;

    private Integer deviceId;

    private String verificationCode;

    //***************************************************************
    //********************************************************METHODS
    //***************************************************************
    public void joinUserNameAndDeviceId(){
        userName = userName + ":" + deviceId;
    }

    public void separateUserNameAndDeviceId(){
        String[] x = userName.split(":");
        userName = x[0];
        deviceId = Integer.parseInt(x[1]);
    }

    public String validateUserName() {
		if (userName != null && RegexUtil.EMAIL.matcher(userName).matches() && userName.length() <= 50) 
			return null;
		return "Credenciales inválidas.";
	}

    public String validatePassword() {
		if (password != null && RegexUtil.PASSWORD.matcher(password).matches()) 
            return null;
		return "Credenciales inválidas.";
	}

    public String validateDeviceId() {
		if (deviceId != null && deviceId.toString().length() == 3) 
            return null;
		return "Dispositivo inválido.";
	}

    public String validateVerificationCode() {
		if (verificationCode != null && Pattern.compile("^[0-9]{3}$").matcher(verificationCode).matches()) 
            return null;
		return "Código de verifición inválido.";
	}

    public String validateLogin() throws Exception {
        String error;
		if ((error = validateUserName()) == null
            && (error = validatePassword()) == null
            && (error = validateDeviceId()) == null) 
			return null;
		return error;
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
