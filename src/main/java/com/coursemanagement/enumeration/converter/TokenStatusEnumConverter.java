package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TokenStatusEnumConverter implements AttributeConverter<TokenStatus, String> {

    @Override
    public String convertToDatabaseColumn(final TokenStatus attribute) {
        return attribute.toDbValue();
    }

    @Override
    public TokenStatus convertToEntityAttribute(final String dbData) {
        return DatabaseUtils.resolveEnum(TokenStatus.class, dbData);
    }
}
