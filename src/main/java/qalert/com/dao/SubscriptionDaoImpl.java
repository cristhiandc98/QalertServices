package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.ISubscription;
import qalert.com.models.BaseData;
import qalert.com.models.subscription.SubscriptionResponse;
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
    public List<SubscriptionResponse> getSubscriptions() {
        
        List<SubscriptionResponse> out = new ArrayList<>();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_GET_SUBSCRIPTIONS);
        
        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        
        SubscriptionResponse model;
        
        for (Map<String,Object> map : resultset) {
            model = new SubscriptionResponse();

            model.setSubscriptionId(DbUtil.getInteger(map, "subscription_id"));
            model.setSubscriptionMonths(DbUtil.getInteger(map, "subscription_months"));
            model.setPriceWithoutDiscount(DbUtil.getFloat(map, "price_without_discount"));
            model.setPriceWithDiscount(DbUtil.getFloat(map, "price_with_discount"));
            model.setDiscountPercentage(DbUtil.getFloat(map, "discount_percentage"));

            out.add(model);
        }

        if(out.size() == 0)
            out = null;

    	return out;
    }



    @Override
    public void updateUserSubscription(long userId) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_UPDATE_USER_SUBSCRIPTION);
        
        SqlParameterSource input = new MapSqlParameterSource()
            .addValue("ni_user_id", userId);

        jdbcCall.execute(input);
    }

}
