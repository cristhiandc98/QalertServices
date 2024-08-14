package qalert.com.utils.utils;

import java.util.regex.Pattern;

public class RegexUtil {

    public static final Pattern SIMPLE_NAME = Pattern.compile("^[0-9A-ZÁÉÍÓÚÑ\\&\\/\\(\\)\\=\\?\\¡\\¿\\!\\,\\;\\:\\.\\{\\}\\[\\]\\| ]{1,100}$");
	public static final Pattern SIMPLE_DESCRIPTION = Pattern.compile("^[0-9A-ZÁÉÍÓÚÑ\\&\\/\\(\\)\\=\\?\\¡\\¿\\!\\,\\;\\:\\.\\{\\}\\[\\]\\| ]{1,250}$");
    public static final Pattern SIMPLE_DATE = Pattern.compile("^[0-9]{4}\\-\\d{2}\\-\\d{2}$");

    public static final Pattern EMAIL = Pattern.compile("^[A-Z0-9.]+@[A-Z0-9]+(\\.[A-Z0-9]+){1,}$");

	public static final Pattern PASSWORD = Pattern.compile("^(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\/-a-zA-Z0-9]).{8,30}$");
	
}
