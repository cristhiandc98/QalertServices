package qalert.com.utils.consts;

import java.sql.Types;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

public class DbParams {

    public static final String DATA = "data";
    
    public static final String BO_STATUS = "bo_estado";
    public static final String VO_USER_MSSG = "vo_user_mssg";
    public static final String VO_ERROR_MSSG = "vo_error_mssg";
    public static final String VO_DATA = "vo_data";
    public static final String OK = "1";

    public static final String NI_USER_ID = "ni_user_id";
    public static final String VI_DESCRIPTION = "vi_description";
    
    //sugerencia
    public static final String NI_ID_MAE_NIVEL_CONFORMIDAD = "ni_id_mae_nivel_conformidad";

    public static final SqlParameter[] PARAM_SALIDA_DATA = new SqlParameter[]{
        new SqlOutParameter(BO_STATUS, Types.BIT)
        ,new SqlOutParameter(VO_ERROR_MSSG, Types.VARCHAR)
        ,new SqlOutParameter(VO_USER_MSSG, Types.VARCHAR)
        ,new SqlOutParameter(VO_DATA, Types.VARCHAR)
    };

}
