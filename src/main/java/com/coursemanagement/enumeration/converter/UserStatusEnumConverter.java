package com.coursemanagement.enumeration.converter;

import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.util.DatabaseUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusEnumConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(final UserStatus attribute) {
        return attribute.toDbValue();
    }

    @Override
    public UserStatus convertToEntityAttribute(final String dbData) {
        return DatabaseUtil.resolveEnum(UserStatus.class, dbData);
    }
}
