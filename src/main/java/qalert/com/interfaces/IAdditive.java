package qalert.com.interfaces;

import java.util.List;

import qalert.com.models.additive.AdditiveResponse;
import qalert.com.models.generic.Response2;

public interface IAdditive {

    Response2<List<AdditiveResponse>> lisAdditive ();
}
