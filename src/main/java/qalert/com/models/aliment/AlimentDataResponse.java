package qalert.com.models.aliment;

import java.util.ArrayList;
import java.util.List;

public class AlimentDataResponse {

    public List<AlimentCategoryResponse> alimentCategoryList = new ArrayList<>();

    public List<AlimentResponse> alimentList = new ArrayList<>();

    public List<AlimentCategoryResponse> getAlimentCategoryList() {
        return alimentCategoryList;
    }

    public void setAlimentCategoryList(List<AlimentCategoryResponse> alimentCategoryList) {
        this.alimentCategoryList = alimentCategoryList;
    }

    public List<AlimentResponse> getAlimentList() {
        return alimentList;
    }

    public void setAlimentList(List<AlimentResponse> alimentList) {
        this.alimentList = alimentList;
    }

}
