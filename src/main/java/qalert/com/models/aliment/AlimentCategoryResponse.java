package qalert.com.models.aliment;

public class AlimentCategoryResponse {

    private int alimentCategoryId;

    private String alimentCategoryName;

    private String imageName;

    public int getAlimentCategoryId() {
        return alimentCategoryId;
    }

    public void setAlimentCategoryId(int alimentCategoryId) {
        this.alimentCategoryId = alimentCategoryId;
    }

    public String getAlimentCategoryName() {
        return alimentCategoryName;
    }

    public void setAlimentCategoryName(String alimentCategoryName) {
        this.alimentCategoryName = alimentCategoryName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}