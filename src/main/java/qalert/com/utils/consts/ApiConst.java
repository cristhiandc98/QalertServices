package qalert.com.utils.consts;

public class ApiConst {

    public static final String PRODUCES = "application/json;charset=UTF-8";

    public static final String ROOT = "/";

    //controller
    public static final String SECURITY = "/security";
    public static final String SCAN = "/scan";
    public static final String USER = "/user";
    public static final String MASTER = "/master";

    //SECURITY
    public static final String GET_VERIFICATION_CODE = "/get-verification-code";
    public static final String LOGIN = "/login";

    //SCAN
    public static final String GET_ADDITIVES_FROM_IMAGE = "/get-additives-from-image";
    public static final String GET_ADDITIVES_REPORT = "/get-additives-report";
    public static final String GET_SCAN_LIST = "/get-scan-list";

    //USER
    public static final String UPDATE_PASSWORD = "/update-password";

    //MASTER
    public static final String LIST_APP_SETTINGS = "/list-app-settings";
    public static final String GET_TERMS_AND_CONDITIONS = "/get-terms-and-conditions";

}
