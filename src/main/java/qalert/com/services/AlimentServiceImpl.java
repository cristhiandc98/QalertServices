package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.IAliment;
import qalert.com.models.aliment.AlimentDataResponse;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class AlimentServiceImpl implements IAliment{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private IAliment alimentDao;

    @Override
    public AlimentDataResponse getAlimentList(long userId) {
        return alimentDao.getAlimentList(userId);
    }

}
