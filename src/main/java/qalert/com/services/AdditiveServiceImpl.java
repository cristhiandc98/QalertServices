package qalert.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IAdditive;
import qalert.com.models.additive.AdditiveResponse;
import qalert.com.models.generic.Response2;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class AdditiveServiceImpl implements IAdditive{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private IAdditive additiveDao;

    @Override
    public Response2<List<AdditiveResponse>> lisAdditive() {
        return additiveDao.lisAdditive();
    }

}
