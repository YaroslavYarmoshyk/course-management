package com.coursemanagement.util;

import com.coursemanagement.enumeration.converter.DatabaseEnum;

import java.util.Arrays;
import java.util.Objects;

public final class DatabaseUtil {

    public static <T extends DatabaseEnum> T resolveEnum(final Class<T> clazz, final Object dbValue) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(enumConstant -> Objects.equals(enumConstant.toDbValue(), dbValue))
                .findAny()
                .orElse(null);
    }
}
