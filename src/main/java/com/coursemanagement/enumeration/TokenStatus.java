package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenStatus implements DatabaseEnum {
    ACTIVATED("A"), NOT_ACTIVATED("N");

    private final String dbAlias;

    @Override
    public String toDbValue() {
        return dbAlias;
    }
}
