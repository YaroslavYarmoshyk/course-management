package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class UserCourseStatusEnumConverter implements AttributeConverter<UserCourseStatus, String> {

    @Override
    public String convertToDatabaseColumn(final UserCourseStatus attribute) {
        return Optional.ofNullable(attribute)
                .map(UserCourseStatus::toDbValue)
                .orElse(null);
    }

    @Override
    public UserCourseStatus convertToEntityAttribute(final String dbData) {
        return DatabaseUtils.resolveEnum(UserCourseStatus.class, dbData);
    }
}
