package qalert.com.models.scan;

import java.util.List;

import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.exceptions.InvalidFormException;
import qalert.com.utils.utils.RegexUtil;

public class ScanRequest {

    private Long userId;

    private Long profileId;

    private List<Long> profileIdList;

    private String productName;

    private Integer reportType;

    private String imageName;

    private Integer scanHeaderId;


    
    public void validateInsert(){

        if(profileIdList == null)
            throw new InvalidFormException("Perfil inválido");

        for (Long id : profileIdList) 
            if (!RegexUtil.validateNumericId(id))
                throw new InvalidFormException("Perfil inválido");
            
        if(productName == null || !RegexUtil.ALLOWED_TEXT_PATTERN.matcher(productName).matches())
            throw new InvalidFormException("Nombre de producto inválido.");
    }



    public void validateGetAdditiveReport(){
        if(!RegexUtil.validateNumericId(profileId) 
            || !RegexUtil.validateNumeric(reportType))
            throw new InvalidFormException(UserMessageConst.BAD_REQUEST);
    }

    
    

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public List<Long> getProfileIdList() {
        return profileIdList;
    }

    public void setProfileIdList(List<Long> profileIdList) {
        this.profileIdList = profileIdList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getScanHeaderId() {
        return scanHeaderId;
    }

    public void setScanHeaderId(Integer scanHeaderId) {
        this.scanHeaderId = scanHeaderId;
    }



    public Long getUserId() {
        return userId;
    }



    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
}
