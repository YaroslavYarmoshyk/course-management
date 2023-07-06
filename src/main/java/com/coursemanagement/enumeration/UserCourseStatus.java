package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserCourseStatus implements DatabaseEnum {
    STARTED("S"), COMPLETED("C");

    private final String dbAlias;

    @Override
    public String toDbValue() {
        return dbAlias;
    }
}
