package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.IMaster;
import qalert.com.models.generic.Response_;
import qalert.com.models.master.MasterResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class MasterDaoImpl implements IMaster{

	@Autowired
	private JdbcTemplate jdbcTemplate;
    

    @Override
    public Response_<List<MasterResponse>> listAppSettings() {
        Response_<List<MasterResponse>> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_list_app_settings");
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET);
        	
            if(resultset != null && !resultset.isEmpty()){

                List<MasterResponse> list = new ArrayList<>();
                MasterResponse model;
                
                for (Map<String,Object> map : resultset) {
                    model = new MasterResponse();

                    model.setTableId(DbUtil.getInteger(map, "table_id"));
                    model.setFieldId(DbUtil.getInteger(map, "field_id"));
                    model.setSequence(DbUtil.getInteger(map, "sequence"));
                    model.setValueInt(DbUtil.getInteger(map, "value_int"));
                    model.setValueVarchar(DbUtil.getString(map, "value_varchar"));

                    list.add(model);
                }
                
                if(!list.isEmpty())
                    out = new Response_<>(list);
                else 
                    out = new Response_<>(HttpStatus.OK, "Configuración no encontrada", false);
            }
            else out = new Response_<>(HttpStatus.OK, "Configuración no encontrada", false);
		
		} catch (Exception ex) {
            out = new Response_<>(ex, null, "Ocurrió un problema al obtener la configuración");
        }		

    	return out;
    }

    
    public Response_<MasterResponse> getTermsAndConditions(){
        Response_<MasterResponse> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_get_terms_and_conditions");
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET);
        	
            if(resultset != null && resultset.size() > 0){

                MasterResponse model = null;
                
                for (Map<String,Object> map : resultset) {
                    model = new MasterResponse();

                    model.setTableId(DbUtil.getInteger(map, "table_id"));
                    model.setFieldId(DbUtil.getInteger(map, "field_id"));
                    model.setSequence(DbUtil.getInteger(map, "sequence"));
                    model.setValueInt(DbUtil.getInteger(map, "value_int"));
                    model.setValueVarchar(DbUtil.getString(map, "value_varchar"));
                }
                
                if(model != null)
                    out = new Response_<>(model);
                else 
                    out = new Response_<>(HttpStatus.OK, "Configuración no encontrada", false);
            }
            else out = new Response_<>(HttpStatus.OK, "Configuración no encontrada", false);
		
		} catch (Exception ex) {
            out = new Response_<>(ex, null, "Ocurrió un problema al obtener la configuración");
        }		

    	return out;
    }
}
