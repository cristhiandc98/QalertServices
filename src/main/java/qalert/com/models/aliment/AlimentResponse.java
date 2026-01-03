package qalert.com.models.aliment;

public class AlimentResponse {

    private int alimentId;

    private int alimentCategoryId;

    private String alimentName;

    private String letter;

    private String description;

    public int getAlimentId() {
        return alimentId;
    }

    public void setAlimentId(int alimentId) {
        this.alimentId = alimentId;
    }

    public int getAlimentCategoryId() {
        return alimentCategoryId;
    }

    public void setAlimentCategoryId(int alimentCategoryId) {
        this.alimentCategoryId = alimentCategoryId;
    }

    public String getAlimentName() {
        return alimentName;
    }

    public void setAlimentName(String alimentName) {
        this.alimentName = alimentName;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

}
