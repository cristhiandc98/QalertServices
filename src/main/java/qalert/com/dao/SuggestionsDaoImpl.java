package qalert.com.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.ISuggestions;
import qalert.com.models.BaseData;
import qalert.com.models.generic.Response2;
import qalert.com.models.suggestions.SuggestionsModel;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class SuggestionsDaoImpl implements ISuggestions{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public Response2<Boolean> insert(SuggestionsModel request) {
    Response2<Boolean> out;

    try {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName(data.getSchema())
            .withProcedureName(DbConst.SP_INSERT_SUGGESTIONS);
        
        SqlParameterSource input = new MapSqlParameterSource()
            .addValue("ni_user_id", request.getUserId())
            .addValue("ni_suggestions_type_id", request.getSuggestionsTypeId())
            .addValue("vi_suggestion", request.getSuggestion());

        jdbcCall.execute(input);

        out = new Response2<>(HttpStatus.CREATED, "Sugerencia insertada exitosamente", true);

    } catch (Exception ex) {
        out = new Response2<>(ex, "Ocurrió un problema al insertar sugerencia");
    }

    return out;
}
}