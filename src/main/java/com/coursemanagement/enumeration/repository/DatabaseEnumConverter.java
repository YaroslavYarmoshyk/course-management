package com.coursemanagement.enumeration.repository;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.util.DatabaseUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DatabaseEnumConverter implements AttributeConverter<TokenType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final TokenType attribute) {
        return attribute.toDbValue();
    }

    @Override
    public TokenType convertToEntityAttribute(final Integer dbData) {
        return DatabaseUtil.resolveEnum(TokenType.class, dbData);
    }
}
