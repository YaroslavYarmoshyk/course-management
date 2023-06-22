package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.LessonPart;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class LessonPartEnumConverter implements AttributeConverter<LessonPart, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final LessonPart attribute) {
        return Optional.ofNullable(attribute)
                .map(LessonPart::toDbValue)
                .orElse(null);
    }

    @Override
    public LessonPart convertToEntityAttribute(final Integer dbData) {
        return DatabaseUtils.resolveEnum(LessonPart.class, dbData);
    }
}
