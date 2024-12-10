package qalert.com.dao;

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

import qalert.com.interfaces.IUser;
import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.profile.ProfileResponse;
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
	
    
    @Override
    public Response2<String> insert(UserRequest request) {
		Response2<String> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_INSERT_USER);
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_password", request.getLogin().getPassword())
                .addValue("ni_device_id", request.getLogin().getDeviceId())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_full_name", request.getFullName())
                .addValue("ni_document_type_id", request.getDocumentTypeId())
                .addValue("vi_document", request.getDocument());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET_1);
            
			out = new Response2<>(HttpStatus.CREATED, 
				DbUtil.getString(resultset.get(0), "user_mssg"), 
				DbUtil.getBoolean(resultset.get(0), "status"));
		
		}catch (Exception ex) {
            out = new Response2<>(ex, request, "Ocurrió un problema al crear el usuario");
        }	

    	return out;
    }


    @Override
    public Response2<UserResponse> login(LoginRequest request){
        Response2<UserResponse> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_LOGIN);
        	
        	SqlParameterSource input = new MapSqlParameterSource()
            .addValue("vi_username", request.getUserName())
            .addValue("ni_device_id", request.getDeviceId());

            Map<String, Object> dbData = jdbcCall.execute(input);
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_2);

            UserResponse user = new UserResponse();
            boolean continue_ = false;

            if(resultset != null && !resultset.isEmpty()){

                ProfileResponse profile;

                for (Map<String,Object> row : resultset) {
                    profile = new ProfileResponse();
                    
                    profile.setProfileId(DbUtil.getInteger(row, "profile_id"));        
                    profile.setName(DbUtil.getString(row, "name"));
                    profile.setIsPrincipal(DbUtil.getBoolean(row, "is_principal"));

                    user.getProfileList().add(profile);
                    user.setFullName(profile.getName());
                    continue_ = true;
                    
                }
            }

            if(continue_){
        	
                resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);
                
                if(resultset != null && !resultset.isEmpty()){
                    Map<String, Object> map = resultset.get(0);
    
                    user.setUserId(DbUtil.getInteger(map, "user_id"));
                        
                    user.getLogin().setUserName(DbUtil.getString(map, "username"));
                    user.getLogin().setPassword(DbUtil.getString(map, "password"));
    
                    out = new Response2<>(user);
                }
                else out = new Response2<>(HttpStatus.UNAUTHORIZED, "Usuario no encontrado", false);
            }
            else out = new Response2<>(HttpStatus.UNAUTHORIZED, "Usuario no encontrado", false);
		
		} catch (Exception ex) {
            out = new Response2<>(ex, request, "Ocurrió un problema al iniciar sesión");
        }		

    	return out;
    }


    @Override
    public Response2<String> updatePassword(UserRequest request) {
        Response2<String> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_update_password");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_username", request.getLogin().getUserName())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("vi_password", request.getLogin().getPassword());
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET_1);
            
			out = new Response2<>(HttpStatus.OK, 
				DbUtil.getString(resultset.get(0), "user_mssg"), 
				DbUtil.getBoolean(resultset.get(0), "status"));
		
		}catch (Exception ex) {
            out = new Response2<>(ex, request, "Ocurrió un problema al actualizar el usuario");
        }	

    	return out;
    }
}
