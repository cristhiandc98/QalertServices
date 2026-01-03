package qalert.com.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import qalert.com.interfaces.log.ILogDao;
import qalert.com.models.BaseData;
import qalert.com.models.generic.Response2;
import qalert.com.models.service_log.LogServiceRequest;

@Repository
public class LogServiceDaoImpl implements ILogDao{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper objectMapper;

    @Autowired
    private BaseData data;

	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public void insert(LogServiceRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName("sp_insert_log_service");
            
            SqlParameterSource input = new MapSqlParameterSource()
                .addValue("user_id", request.getUserId())
                .addValue("profile_id", request.getProfileId())
                .addValue("endpoint", request.getEndPoint())
                .addValue("method", request.getMethod())
                .addValue("http_status_code", request.getHttpStatusCode())
                .addValue("begin_datetime", request.getBeginDateTime())
                .addValue("end_datetime", request.getEndDateTime())
                .addValue("request_header", request.getRequestHeader())
                .addValue("request_body", request.getRequestBody())
                .addValue("response_body", request.getResponseBody())
                .addValue("error_", request.getError());

            jdbcCall.execute(input);

        } catch (Exception ex) {
            try
            {
                String cuerpoSolicitud = request.getRequestBody();
                String cuerpoRespuesta = request.getResponseBody();

                request.setResponseBody(null);
                request.setRequestBody(null);

                String json = objectMapper.writeValueAsString(request);

                logger.error(
                        " | jsonError: " + json +
                        " | cuerpoSolicitud: " + cuerpoSolicitud +
                        " | cuerpoRespuesta: " + cuerpoRespuesta +
                        " | error: " + request.getError());
            }
            catch (Exception e)
            {
                logger.error(new Response2<>(ex).getErrorMssg());
            }
        }
    }
}
