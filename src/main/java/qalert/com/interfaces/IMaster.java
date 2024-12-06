package qalert.com.interfaces;

import java.util.List;

import qalert.com.models.generic.Response2;
import qalert.com.models.master.MasterResponse;

public interface IMaster {

    public Response2<MasterResponse> getTermsAndConditions();

    public Response2<List<MasterResponse>> listAppSettings();

}
