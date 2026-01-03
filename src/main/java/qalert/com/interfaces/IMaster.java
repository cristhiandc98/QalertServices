package qalert.com.interfaces;

import java.util.List;

import qalert.com.models.master.MasterResponse;

public interface IMaster {

    public MasterResponse getTermsAndConditions();

    public List<MasterResponse> getAppSettingsList();

}
