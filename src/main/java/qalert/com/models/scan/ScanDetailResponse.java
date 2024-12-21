package qalert.com.models.scan;

public class ScanDetailResponse {

    private Integer additiveId;

    private String additiveName;

    private Integer total;

    public ScanDetailResponse() {
    }

    public Integer getAdditiveId() {
        return additiveId;
    }

    public void setAdditiveId(Integer additiveId) {
        this.additiveId = additiveId;
    }

    public String getAdditiveName() {
        return additiveName;
    }

    public void setAdditiveName(String additiveName) {
        this.additiveName = additiveName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}
