package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus implements DatabaseEnum {
    ACTIVE("A"), INACTIVE("I");

    private final String dbAlias;

    @Override
    public String toDbValue() {
        return dbAlias;
    }
}
