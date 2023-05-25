package com.coursemanagement.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Kiev");
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static void main(String[] args) {
        DateTimeFormatter dt = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        LocalDateTime now = LocalDateTime.now(DEFAULT_ZONE_ID);
        final String format = now.format(dt);
        System.out.println(format);
    }
}
