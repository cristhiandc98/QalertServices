package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.IAdditive;
import qalert.com.models.BaseData;
import qalert.com.models.additive.AdditiveResponse;
import qalert.com.models.generic.Response2;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class AdditiveDaoImpl implements IAdditive{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public Response2<List<AdditiveResponse>> lisAdditive() {

        Response2<List<AdditiveResponse>> out = new Response2<>();

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_LISTAR_ADDITIVE);

            Map<String, Object> dbData = jdbcCall.execute();
            List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);

                if (resultset != null && !resultset.isEmpty()){
                
                out.setData(new ArrayList<>());
                AdditiveResponse additive;

                for (Map<String, Object> row : resultset) {
                    additive = new AdditiveResponse();

                    additive.setAdditiveId(DbUtil.getInt(row, "additive_id"));
                    additive.setAdditiveGroupId(DbUtil.getInt(row, "additive_group_id"));
                    additive.setToxicityLevelId(DbUtil.getInt(row, "toxicity_level_id"));
                    additive.setName(DbUtil.getString(row, "name"));

                    out.getData().add(additive);
                }
            
            }
        } catch (Exception ex) {
            out = new Response2<>(ex);
        }
        return out;
    }

}
