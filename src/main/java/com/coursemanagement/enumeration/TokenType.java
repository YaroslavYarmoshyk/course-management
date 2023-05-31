package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.repository.DatabaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType implements DatabaseEnum {
    EMAIL_CONFIRMATION(1),
    RESET_PASSWORD(2);

    private final Integer dbAlias;

    @Override
    public Integer toDbValue() {
        return dbAlias;
    }
}
