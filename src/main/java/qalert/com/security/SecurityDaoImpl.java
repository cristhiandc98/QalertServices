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
    		    .withProcedureName("sp_insert_verification_code");
        	
        	SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_user_name", request.getLogin().getUserName())
                .addValue("vi_email", request.getEmail())
                .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                .addValue("is_change_device", isChangeDevice);
        	
			List<Map<String, Object>> dataset = (List<Map<String, Object>>) jdbcCall.execute(input).get(DbConst.RESUL_SET);
            
			out = new Response_<>(HttpStatus.OK, 
				DbUtil.getString(dataset.get(0), "user_mssg"), 
				DbUtil.getBool(dataset.get(0), "status"));
		
		} catch (Exception ex) {
            logger.error((out = new Response_<>(ex, request, "Error al crear usuario")).getErrorMssg());
        }		

    	return out;
	}

	@Override
	public Response_<String> resetIdDevice(UserRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'resetIdDevice'");
	}
   
    /* @Override
    public Respuesta<String> guardarCodigoVerificacion(UsuarioNuevoModel obj) {
        Respuesta<String> salida = new Respuesta<>();
        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_guardar_codigo_verificacion")
				.declareParameters(ParametrosBD.PARAM_SALIDA_DATA);
        	
        	SqlParameterSource input = new MapSqlParameterSource()
        			.addValue("vi_correo", obj.getUsuario().getCorreo())
        			.addValue("vi_usuario", obj.getUsuario().getUsuario())
        			.addValue("vi_codigo_verificacion", obj.getVerificationCode())
        			.addValue("ni_cambio_equipo", obj.getEsCambioEquipo() ? 1 : 0);

			salida = new Respuesta(jdbcCall.execute(input));
			if(salida.getMsjError() != null)
	            logger.error(salida.setError2(obj, "guardarCodigoVerificacion", salida.getMsjError()));

		} catch (Exception ex) {
            salida.setMsjUsuario(MsjUsuario.ERROR_COMUNICAR_BD);
            logger.error(salida.setError2(obj, "guardarCodigoVerificacion", ex.getMessage()));
        }		
    	return salida;
    }
    
    @Override
    public Respuesta<String> resetearIdEquipo(UsuarioNuevoModel obj, String idEquipo) {
        Respuesta<String> salida = new Respuesta<>();
        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_resetear_id_equipo")
				.declareParameters(ParametrosBD.PARAM_SALIDA_DATA);
        	
        	SqlParameterSource input = new MapSqlParameterSource()
        			.addValue("vi_usuario", obj.getUsuario().getUsuario())
        			.addValue("vi_correo", obj.getUsuario().getCorreo())
        			.addValue("vi_codigo_verificacion", obj.getVerificationCode())
        			.addValue("vi_id_equipo", idEquipo);

			salida = new Respuesta(jdbcCall.execute(input));
			if(salida.getMsjError() != null)
	            logger.error(salida.setError2(obj, "resetearIdEquipo", salida.getMsjError()));

		} catch (Exception ex) {
            salida.setMsjUsuario(MsjUsuario.ERROR_COMUNICAR_BD);
            logger.error(salida.setError2(obj, "resetearIdEquipo", ex.getMessage()));
        }		
    	return salida;
    } */
}
