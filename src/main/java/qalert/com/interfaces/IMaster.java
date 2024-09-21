package qalert.com.interfaces;

import qalert.com.models.generic.Response_;
import qalert.com.models.master.MasterResponse;
import java.util.List;

public interface IMaster {

    public Response_<MasterResponse> getTermsAndConditions();

    public Response_<List<MasterResponse>> listAppSettings();

}
