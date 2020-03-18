package eu.europa.ec.mare.usm.administration.rest.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

    private static final String DATE_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date parseDate(String name, String date) {
        Date ret = null;

        if (date != null && !date.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_ZONE_FORMAT);
            try {
                ret = sdf.parse(date);
            } catch (ParseException ex1) {
                SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_TIME_FORMAT);
                try {
                    ret = sdf2.parse(date);
                } catch (ParseException ex2) {
                    SimpleDateFormat sdf3 = new SimpleDateFormat(DATE_FORMAT);
                    try {
                        ret = sdf3.parse(date);
                    } catch (ParseException ex3) {
                        throw new IllegalArgumentException("Unparsable " + name + " date: " + ex3.getMessage());
                    }
                }
            }
        }
        return ret;
    }

}
