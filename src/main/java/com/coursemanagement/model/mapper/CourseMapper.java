package com.coursemanagement.model.mapper;

import com.coursemanagement.enumeration.RoleName;
import com.coursemanagement.model.Course;
import com.coursemanagement.repository.entity.CourseEntity;
import com.coursemanagement.repository.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseEntity modelToEntity(final Course course);

    Course entityToModel(final CourseEntity courseEntity);

    default RoleEntity mapToRoleEntity(RoleName roleName) {
        return new RoleEntity(roleName);
    }

    default RoleName mapToRoleName(RoleEntity roleEntity) {
        return roleEntity.getName();
    }
}
