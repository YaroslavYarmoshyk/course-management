package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class FileTypeEnumConverter implements AttributeConverter<FileType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final FileType attribute) {
        return Optional.ofNullable(attribute)
                .map(FileType::toDbValue)
                .orElse(null);
    }

    @Override
    public FileType convertToEntityAttribute(final Integer dbData) {
        return DatabaseUtils.resolveEnum(FileType.class, dbData);
    }
}
