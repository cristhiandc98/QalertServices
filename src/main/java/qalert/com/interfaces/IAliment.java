package qalert.com.interfaces;

import qalert.com.models.aliment.AlimentDataResponse;

public interface IAliment {

    AlimentDataResponse getAlimentList(long userId);

}
