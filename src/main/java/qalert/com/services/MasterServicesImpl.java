package qalert.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IMaster;
import qalert.com.models.generic.Response2;
import qalert.com.models.master.MasterResponse;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class MasterServicesImpl implements IMaster{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private IMaster dao;

    @Override
    public Response2<List<MasterResponse>> listAppSettings() {
        return dao.listAppSettings();
    }

    @Override
    public Response2<MasterResponse> getTermsAndConditions() {
        return dao.getTermsAndConditions();
    }

}
