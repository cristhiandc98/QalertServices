package qalert.com.models.profile;

import java.time.LocalDate;

import qalert.com.utils.utils.RegexUtil;

public class ProfileRequest {

    private Long profileId;

    private Long userId;

    private String name;

    private LocalDate birthdate;

    private String imagePath;

    public String validateInsert(long id) {

        if(!RegexUtil.validateNumericId(id)){return "Id invalido.";}
        
        if (name == null || !RegexUtil.NAME_PROFILE.matcher(name).matches()) {return "Nombre inválido.";}

        if (birthdate == null || birthdate.isAfter(LocalDate.now()) || birthdate.getYear() < 1900) {return "Fecha de nacimiento inválida.";}
    
        return null; // Todo es válido
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
