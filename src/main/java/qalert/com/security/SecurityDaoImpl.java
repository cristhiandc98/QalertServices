package qalert.com.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import qalert.com.models.generic.Response_;
import qalert.com.models.user.UserRequest;
import qalert.com.utils.consts.Consts;

@Qualifier(Consts.QALIFIER_DAO)
@Repository
public class SecurityDaoImpl implements ISeguridad{
	
	private static final Logger logger = LogManager.getLogger(SecurityDaoImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Response_<String> saveVerificationCode(UserRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'saveVerificationCode'");
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
