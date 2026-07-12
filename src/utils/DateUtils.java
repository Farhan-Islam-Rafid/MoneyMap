package src.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class DateUtils {
    public static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    public static final SimpleDateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isValidDate(String dateStr) {
        try {
            SQL_FORMAT.setLenient(false);
            SQL_FORMAT.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getToday() {
        return LocalDate.now().toString();
    }
}