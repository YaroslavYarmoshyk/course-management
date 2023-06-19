package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.Mark;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.coursemanagement.util.Constants.MARK_CONVERTER_SCALE;

@Converter(autoApply = true)
public class MarkEnumConverter implements AttributeConverter<Mark, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(final Mark attribute) {
        return Optional.ofNullable(attribute)
                .map(Mark::toDbValue)
                .orElse(null);
    }

    @Override
    public Mark convertToEntityAttribute(final BigDecimal dbData) {
        return DatabaseUtils.resolveEnum(Mark.class, dbData.setScale(MARK_CONVERTER_SCALE, RoundingMode.CEILING));
    }
}
