package qalert.com.models.scan;

import org.springframework.http.HttpStatus;

import qalert.com.models.generic.Response2;
import qalert.com.utils.consts.UserMessageConst;
import qalert.com.utils.utils.RegexUtil;

public class ScanRequest {

    private Integer profileId;

    private String productName;

    private Integer reportType;

    public <T> Response2<T> validateInsert(){
        Response2<T> out = new Response2<T>();

        String error = null;

        if(!RegexUtil.validateNumericId(profileId))
            error = UserMessageConst.BAD_REQUEST;
            
        if(productName == null || !RegexUtil.SIMPLE_NAME.matcher(productName).matches())
            error = "Nombre de producto inválido.";

        if(error != null)
            out = new Response2<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);

        return out;
    }

    public <T> Response2<T> validateGetAdditiveReport(){
        Response2<T> out = new Response2<T>();

        String error = null;

        if(!RegexUtil.validateNumericId(profileId) || !RegexUtil.validateNumericId(reportType))
            error = UserMessageConst.BAD_REQUEST;

        if(error != null)
            out = new Response2<>(HttpStatus.BAD_REQUEST, UserMessageConst.BAD_REQUEST);

        return out;
    }

    

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
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
}
