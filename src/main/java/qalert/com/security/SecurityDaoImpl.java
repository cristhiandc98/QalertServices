package qalert.com.security;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.models.generic.Response2;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class SecurityDaoImpl implements ISecurity{

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Response2<String> saveVerificationCode(UserRequest request, boolean isChangeDevice) {
		Response2<String> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_SAVE_VERIFICATION_CODE);
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("is_change_device", isChangeDevice);
        	
			List<Map<String, Object>> dataset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET_1);
            
			out = new Response2<>(HttpStatus.OK, 
				DbUtil.getString(dataset.get(0), "user_mssg"), 
				DbUtil.getString(dataset.get(0), "status").equals("1"));
		
		} catch (Exception ex) {
            out = new Response2<>(ex, "Error al guardar el código de verificación");
        }		

    	return out;
	}

	@Override
	public Response2<String> resetIdDevice(UserRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'resetIdDevice'");
	}
}
