package qalert.com.security;

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

import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.Consts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(Consts.QALIFIER_DAO)
@Repository
public class SecurityDaoImpl implements ISeguridad{
	
	private static final Logger logger = LogManager.getLogger(SecurityDaoImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Response_<String> saveVerificationCode(UserRequest request, boolean isChangeDevice) {
		Response_<String> out = null;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_save_verification_code");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("is_change_device", isChangeDevice);
        	
			List<Map<String, Object>> dataset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(dataset.get(0), "user_mssg"), 
				DbUtil.getBool(dataset.get(0), "status"));
		
		} catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Error al guardar el código de verificación")).getErrorMssg());
        }		

    	return out;
	}

	@Override
	public Response_<String> resetIdDevice(UserRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'resetIdDevice'");
	}
}
