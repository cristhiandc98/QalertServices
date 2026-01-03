package qalert.com.models.additive;

public class AdditiveResponse {

    int additiveId;
    int additiveGroupId;
    int toxicityLevelId;
    String name;

    public int getAdditiveId() {
        return additiveId;
    }

    public void setAdditiveId(int additiveId) {
        this.additiveId = additiveId;
    }

    public int getAdditiveGroupId() {
        return additiveGroupId;
    }

    public void setAdditiveGroupId(int additiveGroupId) {
        this.additiveGroupId = additiveGroupId;
    }

    public int getToxicityLevelId() {
        return toxicityLevelId;
    }

    public void setToxicityLevelId(int toxicityLevelId) {
        this.toxicityLevelId = toxicityLevelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
