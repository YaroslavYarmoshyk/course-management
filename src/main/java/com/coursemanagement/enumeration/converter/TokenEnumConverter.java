package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class TokenEnumConverter implements AttributeConverter<TokenType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final TokenType attribute) {
        return Optional.ofNullable(attribute)
                .map(TokenType::toDbValue)
                .orElse(null);
    }

    @Override
    public TokenType convertToEntityAttribute(final Integer dbData) {
        return DatabaseUtils.resolveEnum(TokenType.class, dbData);
    }
}
