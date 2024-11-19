package qalert.com.utils.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final ZoneId TIME_ZONE = ZoneId.of("America/Lima");
	
	public static ZonedDateTime getZoneDateTime() {      
        return ZonedDateTime.now(TIME_ZONE);
	}

	public static LocalDateTime getCurrentDateTime() {
        return getZoneDateTime().toLocalDateTime();
    }

	public static String generateId() {
        return getZoneDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
	
	public static String getStringCurrentDateTime() {
        return getZoneDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	public static String getStringFirstMonthDay(String cadenafecha) {
		return getZoneDateTime().withDayOfMonth(1).toString();
	}
	
	public static String getStringFirstWeekDay(String cadenafecha) {
		return getZoneDateTime().with(DayOfWeek.MONDAY).toString();
	}
	
	public static String getStringDateTimeFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
	}

}
