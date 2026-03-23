package qalert.com.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.payment.IPaymentDao;
import qalert.com.models.BaseData;
import qalert.com.models.payment.PaymentCreationRequest;
import qalert.com.models.payment.PaymentCreationResponse;
import qalert.com.models.payment.PaymentGetResponse;
import qalert.com.models.payment.PaymentUpdateRequest;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Repository
public class PaymentDaoImpl implements IPaymentDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;



    @Override
    public PaymentCreationResponse insert(PaymentCreationRequest request) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                                .withCatalogName(data.getSchema())
                                .withProcedureName(DbConst.SP_INSERT_PAYMENT);

        SqlParameterSource input = new MapSqlParameterSource()
                        .addValue("ni_user_id", request.getUserId())
                        .addValue("ni_subscription_id", request.getSubscriptionId());

        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET_1);

        PaymentCreationResponse model = new PaymentCreationResponse();
        
        for (Map<String,Object> map : resultset) {
            model.setPaymentId(DbUtil.getLong(map, "payment_id"));
            model.setOrderId(DbUtil.getString(map, "payment_code"));
            model.setAmount(DbUtil.getBigDecimal(map, "amount"));
            model.setCurrency(DbUtil.getString(map, "currency_code"));
        }

    	return model;
    }



    @Override
    public void update(PaymentUpdateRequest request) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                                .withCatalogName(data.getSchema())
                                .withProcedureName(DbConst.SP_UPDATE_PAYMENT);

        SqlParameterSource input = new MapSqlParameterSource()
                        .addValue("ni_payment_id", request.getPaymentId())
                        .addValue("vi_payment_order_id", request.getPaymentOrderId())
                        .addValue("vi_payment_status_name", request.getPaymentStatusCode())
                        .addValue("vi_payment_error", request.getPaymentError());

        jdbcCall.execute(input);
    }



    @Override
    public PaymentGetResponse get(String paymentCode) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                                .withCatalogName(data.getSchema())
                                .withProcedureName(DbConst.SP_GET_PAYMENT);

        SqlParameterSource input = new MapSqlParameterSource()
                        .addValue("vi_payment_code", paymentCode);

        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET_1);

        PaymentGetResponse model = new PaymentGetResponse();
        
        for (Map<String,Object> map : resultset) {
            model.setPaymentId(DbUtil.getLong(map, "payment_id"));
            model.setPaymentOrderId(DbUtil.getString(map, "payment_order_id"));
            model.setOrderId(DbUtil.getString(map, "payment_code"));
            model.setAmount(DbUtil.getBigDecimal(map, "amount"));
            model.setCurrency(DbUtil.getString(map, "currency_code"));
        }

    	return model;
    }

}
