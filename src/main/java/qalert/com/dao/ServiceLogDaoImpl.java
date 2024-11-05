package qalert.com.dao;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.IServiceLog;
import qalert.com.models.generic.Response_;
import qalert.com.models.service_log.ServiceLogRequest;
import qalert.com.utils.consts.Consts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(Consts.QALIFIER_DAO)
@Repository
public class ServiceLogDaoImpl implements IServiceLog{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public Response_<Boolean> insert(ServiceLogRequest request) {
        Response_<Boolean> out = null;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_insert_service_log");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("request_code", request.getRequestCode())
                .addValue("key_", request.getKey_())
                .addValue("key_type", request.getKeyType())
                .addValue("end_point", request.getEndPoint())
                .addValue("http_status_code", request.getHttpStatusCode())
                .addValue("begin_datetime", request.getBeginDateTime())
                .addValue("request_header", request.getRequestHeader())
                .addValue("request_body", request.getRequestBody())
                .addValue("response_body", request.getResponseBody())
                .addValue("error_", request.getError());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.UPDATE_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(resultset.get(0), "user_mssg"), 
				DbUtil.getBool(resultset.get(0), "status"));
		
		}catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Ocurrió un problema al crear el usuario")).getErrorMssg());
        }	

    	return out;
    }

}
