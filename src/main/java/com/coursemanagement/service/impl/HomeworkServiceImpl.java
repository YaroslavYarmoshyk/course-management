package com.coursemanagement.service.impl;

import com.coursemanagement.model.HomeworkSubmission;
import com.coursemanagement.repository.HomeworkRepository;
import com.coursemanagement.repository.entity.HomeworkSubmissionEntity;
import com.coursemanagement.service.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final ModelMapper mapper;

    @Override
    public void save(final HomeworkSubmission homeworkSubmission) {
        final HomeworkSubmissionEntity homeworkEntity = mapper.map(homeworkSubmission, HomeworkSubmissionEntity.class);
        homeworkRepository.save(homeworkEntity);
    }
}
