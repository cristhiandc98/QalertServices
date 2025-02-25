package qalert.com.models.scan;

public class ScanHeaderResponse {

    private Integer scanHeaderId;

    private Integer toxocityLevelId;

    private String toxocityLevel;

    private Integer total;

    private Double percentageOfTotal;

    private String productName;
    
    private String imagePath;


    public ScanHeaderResponse() {
    }

    public Integer getToxocityLevelId() {
        return toxocityLevelId;
    }

    public void setToxocityLevelId(Integer toxocityLevelId) {
        this.toxocityLevelId = toxocityLevelId;
    }

    public String getToxocityLevel() {
        return toxocityLevel;
    }

    public void setToxocityLevel(String toxocityLevel) {
        this.toxocityLevel = toxocityLevel;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(Double percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }

    public Integer getScanHeaderId() {
        return scanHeaderId;
    }

    public void setScanHeaderId(Integer scanHeaderId) {
        this.scanHeaderId = scanHeaderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    
}
