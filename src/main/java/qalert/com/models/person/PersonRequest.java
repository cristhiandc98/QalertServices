package qalert.com.models.person;

import qalert.com.utils.utils.RegexUtil;

public class PersonRequest {

    private String fullName;

    private Integer documentTypeId;

    private String document;

    private String email;

    //***************************************************************
    //*********************************************METHODS
    //***************************************************************
    public String validateEmail() {
        return (email != null && RegexUtil.EMAIL.matcher(email).matches() && email.length() <= 40) ? null : "Email inválido.";
    }



    //***************************************************************
    //*********************************************GETTERS AND SETTER
    //***************************************************************
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Integer documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
