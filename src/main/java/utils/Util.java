package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {
    /**
     * <a href="https://stackoverflow.com/a/201378">https://stackoverflow.com/a/201378</a>
     */
    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static Calendar getCalendar(final Date date) {
        final Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static int getDiffYears(final Date start, final Date end) {
        final Calendar calendarStart = getCalendar(start);
        final Calendar calendarEnd = getCalendar(end);
        final int diff = calendarEnd.get(Calendar.YEAR) - calendarStart.get(Calendar.YEAR);
        if (calendarStart.get(Calendar.MONTH) > calendarEnd.get(Calendar.MONTH) ||
                (calendarStart.get(Calendar.MONTH) == calendarEnd.get(Calendar.MONTH) &&
                        calendarStart.get(Calendar.DATE) > calendarEnd.get(Calendar.DATE))) {
            return diff - 1;
        }
        return diff;
    }
}
