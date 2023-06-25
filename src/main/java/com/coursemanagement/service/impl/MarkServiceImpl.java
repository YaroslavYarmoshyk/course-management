package com.coursemanagement.service.impl;

import com.coursemanagement.model.LessonMark;
import com.coursemanagement.repository.LessonMarkRepository;
import com.coursemanagement.repository.entity.LessonMarkEntity;
import com.coursemanagement.service.MarkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkServiceImpl implements MarkService {
    private final LessonMarkRepository lessonMarkRepository;
    private final ModelMapper mapper;

    @Override
    public LessonMark save(final LessonMark lessonMark) {
        final LessonMarkEntity savedLessonMark = lessonMarkRepository.save(mapper.map(lessonMark, LessonMarkEntity.class));
        return mapper.map(savedLessonMark, LessonMark.class);
    }
}
