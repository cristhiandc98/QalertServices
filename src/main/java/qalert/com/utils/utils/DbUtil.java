package qalert.com.utils.utils;

import java.util.Map;

public class DbUtil {

    public static int getInt(Map<String, Object> data, String key){
        return (int)data.get(key);
    }

    public static Integer getInteger(Map<String, Object> data, String key){
        if(data.get(key) == null)
            return null;
        else
            return getInt(data, key);
    }

    public static String getString(Map<String, Object> data, String key){
        return (String)data.get(key);
    }

}
