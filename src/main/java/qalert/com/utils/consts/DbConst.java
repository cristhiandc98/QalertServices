package qalert.com.utils.consts;

import java.sql.Types;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

public class DbConst {

    public static final String DATA = "data";

    public static final String RESUL_SET = "#result-set-1";
    
    //sugerencia
    public static final String NI_ID_MAE_NIVEL_CONFORMIDAD = "ni_id_mae_nivel_conformidad";

    /* public static final SqlParameter[] PARAM_SALIDA_DATA = new SqlParameter[]{
        new SqlOutParameter(BO_STATUS, Types.BIT)
        ,new SqlOutParameter(VO_ERROR_MSSG, Types.VARCHAR)
        ,new SqlOutParameter(VO_USER_MSSG, Types.VARCHAR)
        ,new SqlOutParameter(VO_DATA, Types.VARCHAR)
    }; */

}
