package qalert.com.dao;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import qalert.com.interfaces.ILogService;
import qalert.com.models.generic.Response_;
import qalert.com.models.service_log.LogServiceRequest;
import qalert.com.utils.consts.CommonConsts;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class LogServiceDaoImpl implements ILogService{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	
	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public void insert(LogServiceRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_insert_log_service");
            
            SqlParameterSource input = new MapSqlParameterSource()
                .addValue("request_code", request.getRequestCode())
                .addValue("key_", request.getKey_())
                .addValue("key_type", request.getKeyType())
                .addValue("method", request.getMethod())
                .addValue("end_point", request.getEndPoint())
                .addValue("http_status_code", request.getHttpStatusCode())
                .addValue("begin_datetime", request.getBeginDateTime())
                .addValue("request_header", request.getRequestHeader())
                .addValue("request_body", request.getRequestBody())
                .addValue("response_body", request.getResponseBody())
                .addValue("error_", request.getError());

            jdbcCall.execute(input);

        } catch (Exception ex) {
            logger.error(new Response_<>(ex).getErrorMssg());

            try
            {
                String cuerpoSolicitud = request.getRequestBody();
                String cuerpoRespuesta = request.getResponseBody();

                request.setResponseBody(null);
                request.setRequestBody(null);

                String json = objectMapper.writeValueAsString(request);

                logger.error(json +
                        " | cuerpoSolicitud: " + cuerpoSolicitud +
                        " | cuerpoRespuesta: " + cuerpoRespuesta +
                        " | error: " + request.getError());
            }
            catch (Exception e)
            {
                logger.error(new Response_<>(ex).getErrorMssg());
            }
        }
    }

    @Override
    public <T> CompletableFuture<Void> save(Response_<T> response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public <T> void savePrivate(Response_<T> response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePrivate'");
    }

    @Override
    public void setRequestData(HttpServletRequest httpRequest, Object request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRequestData'");
    }

}
