package com.coursemanagement.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Kiev");
    public static final String ERROR_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime formatDate(final LocalDateTime localDateTime) {
        final String dateString = localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN);
        return LocalDateTime.parse(dateString, formatter);
    }
}
