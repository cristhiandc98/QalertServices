package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import qalert.com.interfaces.IAliment;
import qalert.com.models.BaseData;
import qalert.com.models.aliment.AlimentCategoryResponse;
import qalert.com.models.aliment.AlimentDataResponse;
import qalert.com.models.aliment.AlimentResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class AlimentDaoImpl implements IAliment{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;



    @Override
    public AlimentDataResponse getAlimentList(long userId) {

        AlimentDataResponse rsp = new AlimentDataResponse();
        

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_GET_ALIMENT_LIST);
        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("user_id", userId);

        Map<String, Object> dbData = jdbcCall.execute(input);


        AlimentCategoryResponse category;
        List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);
        for (Map<String, Object> row : resultset) {

            category = new AlimentCategoryResponse();

            category.setAlimentCategoryId(DbUtil.getInt(row, "aliment_category_id"));
            category.setAlimentCategoryName(DbUtil.getString(row, "aliment_category_name"));
            category.setImageName(DbUtil.getString(row, "image_name"));

            rsp.alimentCategoryList.add(category);
        }


        AlimentResponse aliment;
        resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_2);
        for (Map<String, Object> row : resultset) {

            aliment = new AlimentResponse();

            aliment.setAlimentCategoryId(DbUtil.getInt(row, "aliment_category_id"));
            aliment.setAlimentId(DbUtil.getInt(row, "aliment_id"));
            aliment.setAlimentName(DbUtil.getString(row, "aliment_name"));
            aliment.setLetter(DbUtil.getString(row, "letter"));
            aliment.setDescription(DbUtil.getString(row, "description"));

            rsp.alimentList.add(aliment);
        }


        return rsp;
    }

}
