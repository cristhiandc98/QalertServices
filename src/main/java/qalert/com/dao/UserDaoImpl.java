package qalert.com.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import qalert.com.interfaces.IUser;
import qalert.com.models.BaseData;
import qalert.com.models.generic.Response2;
import qalert.com.models.login.LoginRequest;
import qalert.com.models.profile.ProfileResponse;
import qalert.com.models.subscription.SubscriptionRequest;
import qalert.com.models.user.UserRequest;
import qalert.com.models.user.UserResponse;
import qalert.com.utils.consts.CommonConsts;
import qalert.com.utils.consts.DbConst;
import qalert.com.utils.utils.DbUtil;

@Qualifier(CommonConsts.QALIFIER_DAO)
@Repository
public class UserDaoImpl implements IUser {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BaseData data;

    @Override
    public void insert(UserRequest request) {
        
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(data.getSchema())
                    .withProcedureName(DbConst.SP_INSERT_USER);

            SqlParameterSource input = new MapSqlParameterSource()
                    .addValue("vi_username", request.getLogin().getUserName())
                    .addValue("vi_password", request.getLogin().getPassword())
                    .addValue("ni_device_id", request.getLogin().getDeviceId())
                    .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                    .addValue("vi_email", request.getEmail())
                    .addValue("vi_full_name", request.getFullName())
                    .addValue("ni_document_type_id", request.getDocumentTypeId())
                    .addValue("vi_document", request.getDocument());

             jdbcCall.execute(input);
    }

    @Override
    public Response2<UserResponse> login(LoginRequest request) {
        Response2<UserResponse> out;

        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(data.getSchema())
                    .withProcedureName(DbConst.SP_LOGIN);

            SqlParameterSource input = new MapSqlParameterSource()
                    .addValue("vi_username", request.getUserName())
                    .addValue("ni_device_id", request.getDeviceId());

            Map<String, Object> dbData = jdbcCall.execute(input);

            List<Map<String, Object>> resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_2);

            UserResponse user = new UserResponse();
            boolean continue_ = false;

            if (resultset != null && !resultset.isEmpty()) {

                ProfileResponse profile;

                for (Map<String, Object> row : resultset) {
                    profile = new ProfileResponse();

                    profile.setProfileId(DbUtil.getLong(row, "profile_id"));
                    profile.setName(DbUtil.getString(row, "name"));
                    profile.setIsPrincipal(DbUtil.getBoolean(row, "is_principal"));
                    profile.setImagePath(DbUtil.getString(row, "image_path"));

                    user.getProfileList().add(profile);
                    user.setFullName(profile.getName());
                    continue_ = true;

                }
            }

            if (continue_) {

                resultset = (List<Map<String, Object>>) dbData.get(DbConst.RESUL_SET_1);

                if (resultset != null && !resultset.isEmpty()) {
                    Map<String, Object> map = resultset.get(0);

                    user.setUserId(DbUtil.getLong(map, "user_id"));

                    user.getLogin().setUserName(DbUtil.getString(map, "username"));
                    user.getLogin().setPassword(DbUtil.getString(map, "password"));
                    user.setFullName(DbUtil.getString(map, "full_name"));
                    user.setDocumentTypeId(DbUtil.getInteger(map, "document_type_id"));
                    user.setDocument(DbUtil.getString(map, "document"));
                    user.setEmail(DbUtil.getString(map, "email"));
                    user.setSubscriptionId(DbUtil.getInteger(map, "subscription_id"));

                    out = new Response2<>(user);
                } else {
                    out = new Response2<>(HttpStatus.UNAUTHORIZED, "Usuario no encontrado", false);
                }
            } else {
                out = new Response2<>(HttpStatus.UNAUTHORIZED, "Usuario no encontrado", false);
            }

        } catch (Exception ex) {
            out = new Response2<>(ex, "Ocurrió un problema al iniciar sesión");
        }

        return out;
    }

    @Override
    public void updatePassword(UserRequest request) {

            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(data.getSchema())
                    .withProcedureName("sp_update_password");

            SqlParameterSource input = new MapSqlParameterSource()
                    .addValue("vi_username", request.getLogin().getUserName())
                    .addValue("vi_verification_code", request.getLogin().getVerificationCode())
                    .addValue("vi_password", request.getLogin().getPassword());

        jdbcCall.execute(input);
    }

    @Override
    public Response2<String> validateNewUser(UserRequest request) {
        Response2<String> out;

        try {
            String sql = "SELECT fn_validate_new_user(?)";
            Boolean exists = jdbcTemplate.queryForObject(sql, new Object[] {
                    request.getLogin().getUserName()
            }, Boolean.class);

            if (Boolean.TRUE.equals(exists)) {
                out = new Response2<>(HttpStatus.OK, "El nombre de usuario ya se encuentra registrado", false);
            } else {
                out = new Response2<>(HttpStatus.OK, "Usuario disponible", true);
            }

        } catch (Exception ex) {
            out = new Response2<>(ex, "Error al validar si el usuario existe");
        }

        return out;
    }

}
