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
import qalert.com.models.BaseData;
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
    
    @Autowired
    private BaseData data;

    @Override
    public List<MasterResponse> getAppSettingsList() {
        List<MasterResponse> out = new ArrayList<>();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_GET_APP_SETTINGS_LIST);
        
        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        
        MasterResponse model;
        
        for (Map<String,Object> map : resultset) {
            model = new MasterResponse();

            model.setTableId(DbUtil.getInteger(map, "table_id"));
            model.setFieldId(DbUtil.getInteger(map, "field_id"));
            model.setSequence(DbUtil.getInteger(map, "sequence"));
            model.setValueInt(DbUtil.getInteger(map, "value_int"));
            model.setDescription(DbUtil.getString(map, "description"));

            out.add(model);
        }

        if(out.isEmpty())
            out = null;

    	return out;
    }


    
    public MasterResponse getTermsAndConditions(){

        MasterResponse out = null;

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_GET_TERMS_AND_CONDITIONS);
        
        List<Map<String, Object>> resultset = (List<Map<String, Object>>) jdbcCall.execute().get(DbConst.RESUL_SET_1);
        
        
        for (Map<String,Object> map : resultset) {
            out = new MasterResponse();

            out.setTableId(DbUtil.getInteger(map, "table_id"));
            out.setFieldId(DbUtil.getInteger(map, "field_id"));
            out.setSequence(DbUtil.getInteger(map, "sequence"));
            out.setValueInt(DbUtil.getInteger(map, "value_int"));
            out.setValueVarchar(DbUtil.getString(map, "value_varchar"));
        }

    	return out;
    }
}
