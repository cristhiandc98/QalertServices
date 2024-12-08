package qalert.com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.scan.IScanDao;
import qalert.com.models.generic.Response2;
import qalert.com.utils.consts.DbConst;

@Repository
public class ScanDaoImpl implements IScanDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

    @Override
    public Response2<Boolean> insertAndGetAdditivesFromPlainText(int profileId, String data) {
        Response2<Boolean> out = new Response2<>();

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_INSERT_AND_GET_ADDITIVES_FROM_PLAIN_TEXT);

            SqlParameterSource input = new MapSqlParameterSource()
                .addValue("ni_profile_id", profileId)
                .addValue("vi_data", data);
        	
                jdbcCall.execute(input);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        return out;
    }

}
