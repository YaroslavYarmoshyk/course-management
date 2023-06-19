package com.coursemanagement.util;

import java.time.ZoneId;

public final class DateTimeUtils {
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Europe/Kiev");
    public static final String ERROR_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
