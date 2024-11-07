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

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response_;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.login.LoginResponse;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class UserDaoImpl implements IUser{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public Response_<String> insert(UserRequest request) {
		Response_<String> out = null;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_insert_user");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_password", request.getLogin().getPassword())
                .addValue("ni_device_id", request.getLogin().getDeviceId())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_full_name", request.getFullName())
                .addValue("ni_document_type_id", request.getDocumentTypeId())
                .addValue("vi_document", request.getDocument());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(resultset.get(0), "user_mssg"), 
				DbUtil.getBool(resultset.get(0), "status"));
		
		}catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Ocurrió un problema al crear el usuario")).getErrorMssg());
        }	

    	return out;
    }

    public Response_<UserResponse> login(LoginRequest request){
        Response_<UserResponse> out = null;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_login");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
            .addValue("vi_username", request.getUserName())
            .addValue("ni_device_id", request.getDeviceId());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
        	
            if(resultset != null && resultset.size() > 0){
                Map<String, Object> map = resultset.get(0);

                UserResponse user = new UserResponse();
                LoginResponse login = new LoginResponse();

                user.setUserId(DbUtil.getInteger(map, "user_id"));
                user.setFullName(DbUtil.getString(map, "full_name"));

                login.setPassword(DbUtil.getString(map, "username"));
                login.setPassword(DbUtil.getString(map, "password"));

                user.setLogin(login);

                out = new Response_<>(user);
            }
            else out = new Response_<>(HttpStatus.UNAUTHORIZED, "Usuario no encontrado", false);
		
		} catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Ocurrió un problema al iniciar sesión")).getErrorMssg());
        }		

    	return out;
    }

    @Override
    public Response_<String> updatePassword(UserRequest request) {
        Response_<String> out = null;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_update_password");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("vi_password", request.getLogin().getPassword());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(resultset.get(0), "user_mssg"), 
				DbUtil.getBool(resultset.get(0), "status"));
		
		}catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Ocurrió un problema al actualizar el usuario")).getErrorMssg());
        }	

    	return out;
    }
}
