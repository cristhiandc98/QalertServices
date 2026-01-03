package qalert.com.utils.utils;

import java.util.regex.Pattern;

public class RegexUtil {

    public static final Pattern SIMPLE_NAME = Pattern.compile("^[0-9A-Za-zÁÉÍÓÚÑ\\&\\/\\(\\)\\=\\?\\¡\\¿\\!\\,\\;\\:\\.\\{\\}\\[\\]\\| ]{1,100}$");
	public static final Pattern SIMPLE_DESCRIPTION = Pattern.compile("^[0-9A-Za-zÁÉÍÓÚÑ\\&\\/\\(\\)\\=\\?\\¡\\¿\\!\\,\\;\\:\\.\\{\\}\\[\\]\\| ]{1,250}$");
    public static final Pattern SIMPLE_DATE = Pattern.compile("^[0-9]{4}\\-\\d{2}\\-\\d{2}$");

    public static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9.-]+@[A-Za-z0-9]+(\\.[A-Za-z0-9]+){1,}$");

	public static final Pattern PASSWORD = Pattern.compile("^(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\/-a-zA-Z0-9]).{8,20}$");
	
    public static final Pattern NUMBER = Pattern.compile("^[0-9]+$");

    // Regex profile
    public static final Pattern NAME_PROFILE = Pattern.compile("^[\\p{L}0-9\\s]{2,20}$");

    // Regex Suggestions
    public static final Pattern SUGGESTION_FIELD = Pattern.compile("^[\\p{L}\\p{N}\\p{P}\\p{Z}\\n\\r\\t]{10,1000}$");

    public static boolean validateNumeric(Integer number){
        return number != null && number >= 0;
    }

    public static boolean validateNumericId(Long number) {
    return number != null && number >= 0;
    }

}
