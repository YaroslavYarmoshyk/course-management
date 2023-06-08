package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

@Converter(autoApply = true)
public class MarkEnumConverter implements AttributeConverter<Mark, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(final Mark attribute) {
        return attribute.toDbValue();
    }

    @Override
    public Mark convertToEntityAttribute(final BigDecimal dbData) {
        return DatabaseUtils.resolveEnum(Mark.class, dbData);
    }
}
