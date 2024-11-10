package qalert.com.models.user;

import java.util.regex.Pattern;

import qalert.com.models.login.LoginRequest;
import qalert.com.models.person.PersonRequest;

public class UserRequest extends PersonRequest{

    private Integer userId;
    
    private LoginRequest login;

    
    //***************************************************************
    //********************************************************METHODS
    //***************************************************************    
    public String validateLogin(){
        return (getLogin() == null) ? "Credenciales inválidas." : null;
    }

    public String validateFormVerificationCode() {         
        String error;

        if((error = validateLogin()) == null
            && (error = getLogin().validateUserName()) == null
            && (error = validateEmail()) == null)
            return null;
        return error;
    }

    public String validateUserRegister() {            
        String error;
        String fullName = getFullName();
        Integer documentTypeId = getDocumentTypeId();
        String document = getDocument();

        if ((error = validateLogin()) == null
            && (error = getLogin().validateUserName()) == null
            && (error = validateEmail()) == null
            && (error = getLogin().validatePassword()) == null
            && (error = (fullName != null && Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ' -]{2,50}$").matcher(fullName).matches()) ? null : "Nombre inválido") == null
            && (error = (documentTypeId != null && documentTypeId > 0) ? null : "Tipo de documento inválido") == null
            && (error = (document != null && Pattern.compile("^[a-zA-Z0-9:;<>,\\!\\@#\\$\\%\\^&\\*\\(\\)_\\+\\{\\}\\[\\]\\.\\?\\/\\-]{6,20}$").matcher(document).matches()) ? null : "Documento inválido") == null) 
            return null;
		return error;
	}

    public String validateUpdatePassword() {
        String error;
		if ((error = validateLogin()) == null
            && (error = getLogin().validateUserName()) == null
            && (error = getLogin().validatePassword()) == null
            && (error = getLogin().validateVerificationCode()) == null) 
			return null;
		return error;
	}


    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public LoginRequest getLogin() {
        return login;
    }

    public void setLogin(LoginRequest login) {
        this.login = login;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }    
    
}
