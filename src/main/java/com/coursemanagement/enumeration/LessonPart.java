package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LessonPart implements DatabaseEnum {
    INTRODUCTION(1), LEARNING_OBJECTIVES(2), ACTIVITIES(3), PRACTICE(4);

    private final Integer dbAlias;

    @Override
    public Integer toDbValue() {
        return dbAlias;
    }
}
