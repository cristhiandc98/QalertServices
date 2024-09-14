package qalert.com.models.user;

import java.util.regex.Pattern;

import qalert.com.models.login.LoginRequest;
import qalert.com.models.person.PersonRequest;
import qalert.com.utils.utils.RegexUtil;

public class UserRequest extends PersonRequest{

    private Integer userId;
    
    private LoginRequest login;

    
    //***************************************************************
    //********************************************************METHODS
    //***************************************************************    
    public boolean validateFormVerificationCode() throws Exception {
        String email = getEmail();
        if(getLogin() != null
            && getLogin().validateUserName()
            && email != null && RegexUtil.EMAIL.matcher(email).matches() && email.length() <= 50)
            return true;
        return false;
    }

    public boolean validateUserRegister() throws Exception {
        if(getLogin() == null) return false;
        
        String email = getEmail();
        String fullName = getFullName();
        Integer documentTypeId = getDocumentTypeId();
        String document = getDocument();

		if (getLogin().validateUserName() 
            && email != null && RegexUtil.EMAIL.matcher(email).matches() && email.length() <= 50
            && getLogin().validatePassword()
            && fullName != null && Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ' -]{2,50}$").matcher(fullName).matches()
            && documentTypeId != null && documentTypeId > 0
            && document != null && Pattern.compile("^[a-zA-Z0-9:;<>,\\!\\@#\\$\\%\\^&\\*\\(\\)_\\+\\{\\}\\[\\]\\.\\?\\/\\-]{6,20}$").matcher(document).matches()
            ) 
			return true;
		return false;
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
