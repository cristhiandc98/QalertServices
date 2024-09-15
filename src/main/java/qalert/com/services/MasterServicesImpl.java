package qalert.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IMaster;
import qalert.com.models.generic.Response_;
import qalert.com.models.master.MasterResponse;
import qalert.com.utils.consts.Consts;

@Qualifier(Consts.QALIFIER_SERVICE)
@Service
public class MasterServicesImpl implements IMaster{

    @Qualifier(Consts.QALIFIER_DAO)
    @Autowired
    private IMaster dao;

    @Override
    public Response_<List<MasterResponse>> listAppSettings() {
        return dao.listAppSettings();
    }

}
