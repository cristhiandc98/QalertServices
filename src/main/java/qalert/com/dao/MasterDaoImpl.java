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
import qalert.com.models.generic.Response2;
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
    public Response2<List<MasterResponse>> listAppSettings() {
        Response2<List<MasterResponse>> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName(DbConst.SP_LIST_APP_SETTINGS);
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        	
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
                    out = new Response2<>(list);
                else 
                    out = new Response2<>(HttpStatus.OK, "Configuración no encontrada", false);
            }
            else out = new Response2<>(HttpStatus.OK, "Configuración no encontrada", false);
		
		} catch (Exception ex) {
            out = new Response2<>(ex, null, "Ocurrió un problema al obtener la configuración");
        }		

    	return out;
    }

    
    public Response2<MasterResponse> getTermsAndConditions(){
        Response2<MasterResponse> out;

        try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    		    .withProcedureName("sp_get_terms_and_conditions");
        	
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        	
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
                    out = new Response2<>(model);
                else 
                    out = new Response2<>(HttpStatus.OK, "Configuración no encontrada", false);
            }
            else out = new Response2<>(HttpStatus.OK, "Configuración no encontrada", false);
		
		} catch (Exception ex) {
            out = new Response2<>(ex, null, "Ocurrió un problema al obtener la configuración");
        }		

    	return out;
    }
}
