package qalert.com.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
import qalert.com.utils.consts.Consts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(Consts.QALIFIER_DAO)
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
                .addValue("vi_user_name", request.getLogin().getUserName())
                .addValue("vi_password", request.getLogin().getPassword())
                .addValue("ni_device_id", request.getLogin().getDeviceId())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_full_name", request.getFullName())
                .addValue("ni_document_type_id", request.getDocumentTypeId())
                .addValue("vi_document", request.getDocument());
        	
            List<Map<String, Object>> dataset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(dataset.get(0), "user_mssg"), 
				DbUtil.getBool(dataset.get(0), "status"));
		
		}catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Error al crear usuario")).getErrorMssg());
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
            .addValue("vi_device_id", request.getDeviceId());

            List<Map<String, Object>> dataSet = (List<Map<String, Object>>) jdbcCall.execute(input).entrySet().iterator().next().getValue();
        	
            if(dataSet != null && dataSet.size() > 0){
                Map<String, Object> map = dataSet.get(0);

                UserResponse user = new UserResponse();
                LoginResponse login = new LoginResponse();

                user.setUserId(DbUtil.getInteger(map, "user_id"));
                user.setFullName(DbUtil.getString(map, "full_name"));
                user.setDocumentTypeId(DbUtil.getInteger(map, "document_type_id"));
                user.setDocument(DbUtil.getString(map, "document"));
                user.setEmail(DbUtil.getString(map, "email"));
                user.setBirthDay(DbUtil.getString(map, "birthday"));

                login.setPassword(DbUtil.getString(map, "username"));
                login.setPassword(DbUtil.getString(map, "password"));
                login.setDeviceId(DbUtil.getInteger(map, "device_id"));

                user.setLogin(login);

                out = new Response_<>(user);
            }
            else out = new Response_<>(HttpStatus.UNAUTHORIZED, "Usuario no registrado", false);
		
		} catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Error iniciar sesión")).getErrorMssg());
        }		

    	return out;
    }
}
