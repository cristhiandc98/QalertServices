package qalert.com.utils.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class DbUtil {

    public static int getInt(Map<String, Object> data, String key) {
        if (data.get(key) == null) {
            return 0;
        }
        return (int) data.get(key);
    }

    public static Integer getInteger(Map<String, Object> data, String key) {
        Object value = data.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        //  if (value instanceof Number) {
        //      return ((Number) value).intValue(); // Para casos como Long, BigDecimal, etc.
        //  }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            // Log o manejo personalizado si se desea
            return null;
        }
    }

    public static Long getLong(Map<String, Object> data, String key) {
        if (data.get(key) == null) {
            return null; 
        }else {
            return (Long) data.get(key);
        }
    }

    public static String getString(Map<String, Object> row, String key) {
        if (row == null || key == null || !row.containsKey(key)) {
            return null;
        }

        Object value = row.get(key);
        if (value == null) {
            return null;
        }

        // Si es una fecha, conviértela a String (formato ISO)
        if (value instanceof java.sql.Date) {
            return value.toString(); // Esto da formato "yyyy-MM-dd"
        }

        if (value instanceof java.time.LocalDate) {
            return value.toString(); // También "yyyy-MM-dd"
        }

        // Si es cualquier otro tipo de objeto, intenta usar toString()
        return value.toString();
    }

    public static boolean getBoolean(Map<String, Object> data, String key) {
        if (data.get(key) == null) {
            return false;
        }
        return (boolean) data.get(key);
    }

    public static float getFloat(Map<String, Object> data, String key) {
        if (data.get(key) == null) {
            return 0f;
        }
        return ((Number) data.get(key)).floatValue();
    }

    public static LocalDate getLocalDate(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return null;

        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
        }
        // Intenta parsear de String
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static BigDecimal getBigDecimal(Map<String, Object> data, String key) {
        Object value = data.get(key);

        if (value == null) {
            return BigDecimal.ZERO; // o null si prefieres
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        return new BigDecimal(value.toString());
    }
}
