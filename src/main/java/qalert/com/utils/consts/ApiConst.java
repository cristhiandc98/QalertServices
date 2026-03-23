package qalert.com.utils.consts;

public class ApiConst {

    public static final String PRODUCES = "application/json;charset=UTF-8";

    public static final String ROOT = "/";

    // controller
    public static final String SECURITY = "/security";
    public static final String SCAN = "/scan";
    public static final String USER = "/user";
    public static final String MASTER = "/master";
    public static final String PROFILE = "/profile";
    public static final String SUGGESTIONS = "/suggestions";
    public static final String ADDITIVE = "/additive";
    public static final String ALIMENT = "/aliment";
    public static final String PAYMENT = "/payment";
    public static final String SUBSCRIPTION = "/subscription";

    // SECURITY
    public static final String GET_VERIFICATION_CODE = "/get-verification-code";
    public static final String LOGIN = "/login";

    // SCAN
    public static final String GET_ADDITIVES_FROM_IMAGE = "/get-additives-from-image";
    public static final String GET_ADDITIVES_REPORT = "/get-additives-report";
    public static final String GET_SCAN_LIST = "/get-scan-list";
    public static final String RENAME_SCAN = "/rename-scan";
    public static final String DELETE_SCAN = "/delete-scan";

    // USER
    public static final String UPDATE_PASSWORD = "/update-password";
    public static final String EXISTING_USER = "/existing-user";

    // MASTER
    public static final String GET_APP_SETTINGS = "/get-app-settings";
    public static final String GET_TERMS_AND_CONDITIONS = "/get-terms-and-conditions";

    // PROFILE
    public static final String INSERT_PROFILE = "/insert";
    public static final String LIST_PROFILES = "/fetch-by-user-id";
    public static final String UPDATE_PROFILE = "/update";

    // Suggestions
    public static final String INSERT_SUGGESTIONS = "/insert";

    //Subcripstion  

    //WEB SOCKET
    public static final String WS_ENDPOINT = "/ws";
    public static final String WS_SERVER_PREFIX = "/app";
    public static final String WS_CLIENT_PREFIX = "/payment";
}
