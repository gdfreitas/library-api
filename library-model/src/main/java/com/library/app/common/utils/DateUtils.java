package com.library.app.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gabriel.freitas
 */
public final class DateUtils {

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private DateUtils() {
    }

    public static Date getAsDateTime(final String dateTime) {
        try {
            return new SimpleDateFormat(FORMAT).parse(dateTime);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String formatDateTime(final Date date) {
        return new SimpleDateFormat(FORMAT).format(date);
    }

}
