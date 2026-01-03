package qalert.com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.IQalert;
import qalert.com.models.BaseData;
import qalert.com.models.subscription.SubscriptionModel;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class QalertDaoImpl implements IQalert {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public void subscribe(Long user_id,SubscriptionModel request) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName(DbConst.SP_SUBSCRIPTION);

        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_user_id", user_id)
                .addValue("vi_subscription_id", request.getSubscriptionId());

        // Ejecutar el SP (no devuelve resultsets)
        jdbcCall.execute(input);
    }

}
