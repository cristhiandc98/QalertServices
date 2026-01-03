package qalert.com.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.profile.profileDao;
import qalert.com.models.BaseData;
import qalert.com.models.generic.Response2;
import qalert.com.models.profile.ProfileRequest;
import qalert.com.models.profile.ProfileResponse;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Repository
public class ProfileDaoImpl implements profileDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public void insert(ProfileRequest request) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName(DbConst.SP_INSERT_PROFILE);

        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_user_id", request.getUserId())
                .addValue("vi_name", request.getName())
                .addValue("vi_birthdate", request.getBirthdate())
                .addValue("vi_image_path", request.getImagePath());

        jdbcCall.execute(input);
    }

    @Override
    public void updateProfile(ProfileRequest request) {
        // Response2<String> out;

        // try {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName(DbConst.SP_UPDATE_PROFILE)
                .returningResultSet("out", new ColumnMapRowMapper());

        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_profile_id", request.getProfileId())
                .addValue("vi_name", request.getName())
                .addValue("vi_birthdate", request.getBirthdate())
                .addValue("vi_image_path", request.getImagePath());

        jdbcCall.execute(input);
    }

    @Override
    public void deleteProfile(Long profileId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName(DbConst.SP_DELETE_PROFILE);

        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_profile_id", profileId);

        jdbcCall.execute(input);
    }

    @Override
    public List<ProfileResponse> listProfiles(Long userId) {
        List<ProfileResponse> list_profile = new ArrayList<>();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName(data.getSchema())
                .withProcedureName(DbConst.SP_LIST_PROFILES);

        SqlParameterSource input = new MapSqlParameterSource()
                .addValue("vi_user_id", userId);

        Map<String, Object> dbData = jdbcCall.execute(input);

        List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);

        ProfileResponse Profile;

        for (Map<String, Object> row : resultset) {
            Profile = new ProfileResponse();

            Profile.setProfileId(DbUtil.getLong(row, "profile_id"));
            Profile.setUserId(DbUtil.getLong(row, "user_id"));
            Profile.setName(DbUtil.getString(row, "name"));
            Profile.setAge(DbUtil.getInteger(row, "age"));
            Profile.setBirthdate(DbUtil.getLocalDate(row, "birthdate"));
            Profile.setImagePath(DbUtil.getString(row, "image_path"));
            Profile.setIsPrincipal(DbUtil.getBoolean(row, "is_principal"));

            list_profile.add(Profile);
        }

        return list_profile;
    }

}
