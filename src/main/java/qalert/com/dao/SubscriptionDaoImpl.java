package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.ISubscription;
import qalert.com.models.BaseData;
import qalert.com.models.subscription.SubscriptionLabelsResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class SubscriptionDaoImpl implements ISubscription{

	@Autowired
	private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private BaseData data;



    @Override
    public List<SubscriptionLabelsResponse> getAll() {
        
        List<SubscriptionLabelsResponse> out = new ArrayList<>();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_SUBSCRIPTION_LIST);
        
        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        
        SubscriptionLabelsResponse model;
        
        for (Map<String,Object> map : resultset) {
            model = new SubscriptionLabelsResponse();

            model.setSubscriptionId(DbUtil.getInteger(map, "subscription_id"));
            model.setSubscriptionMonths(DbUtil.getString(map, "subscription_months"));
            model.setPrice(DbUtil.getString(map, "price"));
            model.setDiscountedPrice(DbUtil.getString(map, "discounted_price"));
            model.setDiscountPercentage(DbUtil.getString(map, "discount_percentage"));

            out.add(model);
        }

        if(out.size() == 0)
            out = null;

    	return out;
    }

}
