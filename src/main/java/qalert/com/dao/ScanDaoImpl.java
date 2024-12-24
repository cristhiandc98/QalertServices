package qalert.com.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.scan.IScanDao;
import qalert.com.models.generic.Response2;
import qalert.com.models.scan.ScanDetailResponse;
import qalert.com.models.scan.ScanHeaderResponse;
import qalert.com.models.scan.ScanResponse;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Repository
public class ScanDaoImpl implements IScanDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

    @Override
    public Response2<ScanResponse> insertAndGetAdditivesFromPlainText(int profileId, String data) {
        Response2<ScanResponse> out = new Response2<>();
        out.setData(new ScanResponse());

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_INSERT_AND_GET_ADDITIVES_FROM_PLAIN_TEXT);

            SqlParameterSource input = new MapSqlParameterSource()
                .addValue("ni_profile_id", profileId)
                .addValue("vi_data", data);
        	

            Map<String, Object> dbData = jdbcCall.execute(input);
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);

            boolean continue_ = false;

            if(resultset != null && !resultset.isEmpty()){
                
                ScanHeaderResponse header; 

                for (Map<String,Object> row : resultset) {

                    header = new ScanHeaderResponse();

                    header.setToxocityLevelId(DbUtil.getInteger(row, "toxicity_level_id"));
                    header.setToxocityLevel(DbUtil.getString(row, "toxicity_level"));
                    header.setTotal(DbUtil.getLong(row, "total").intValue());

                    out.getData().addScanHeader(header);
                }
                continue_ = true;
            }

            if(continue_){

                resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_2);

                ScanDetailResponse detail;

                for (Map<String,Object> row : resultset){

                    detail = new ScanDetailResponse();

                    detail.setAdditiveId(DbUtil.getInteger(row, "toxicity_level_id"));
                    detail.setAdditiveName(DbUtil.getString(row, "aditive_name_or_code"));
                    detail.setTotal(DbUtil.getLong(row, "total").intValue());
                    detail.setToxicityLevelId(DbUtil.getInteger(row, "toxicity_level_id"));

                    out.getData().addScanDetail(detail);
                }
            }
            else out = new Response2<>(HttpStatus.OK, "Aditivos no encontrados", false);

        } catch (Exception ex) {
            out = new Response2<>(ex);
        }

        return out;
    }

}
