package qalert.com.models.scan;

public class ScanHeaderResponse {

    private Integer toxocityLevelId;

    private String toxocityLevel;

    private Integer total;

    private double percentageOfTotal;

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

    public double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(double percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }

}
