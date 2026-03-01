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
import qalert.com.models.payment.PaymentRequest;
import qalert.com.utils.consts.DbConst;

@Repository
public class PaymentDaoImpl implements IPaymentDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public void insert(PaymentRequest request) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                                .withCatalogName(data.getSchema())
                                .withProcedureName(DbConst.SP_INSERT_PAYMENT);

        SqlParameterSource input = new MapSqlParameterSource()
                        .addValue("ni_user_id", request.getUserId())
                        .addValue("amount", request.getAmount());

        jdbcCall.execute(input);
    }

}
