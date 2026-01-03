package qalert.com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import qalert.com.interfaces.ISuggestions;
import qalert.com.models.generic.Response2;
import qalert.com.models.suggestions.SuggestionsModel;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_SERVICE)
@Service
public class SuggestionsServiceImpl implements ISuggestions{

    @Qualifier(CommonConsts.QALIFIER_DAO)
    @Autowired
    private ISuggestions dao;

    @Override
    public Response2<Boolean> insert(SuggestionsModel request) {
        return dao.insert(request);
    }

}
