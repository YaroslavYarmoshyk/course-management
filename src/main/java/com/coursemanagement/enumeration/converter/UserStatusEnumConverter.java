package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.util.DatabaseUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class UserStatusEnumConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(final UserStatus attribute) {
        return Optional.ofNullable(attribute)
                .map(UserStatus::toDbValue)
                .orElse(null);
    }

    @Override
    public UserStatus convertToEntityAttribute(final String dbData) {
        return DatabaseUtils.resolveEnum(UserStatus.class, dbData);
    }
}
