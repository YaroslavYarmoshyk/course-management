package com.coursemanagement.model.mapper;

import com.coursemanagement.model.ConfirmationToken;
import com.coursemanagement.repository.entity.ConfirmationTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConfirmationTokenMapper {

    @Mapping(source = "userId", target = "userEntity.id")
    ConfirmationTokenEntity modelToEntity(final ConfirmationToken confirmationToken);

    @Mapping(source = "userEntity.id", target = "userId")
    ConfirmationToken modelToModel(final ConfirmationTokenEntity confirmationTokenEntity);
}
