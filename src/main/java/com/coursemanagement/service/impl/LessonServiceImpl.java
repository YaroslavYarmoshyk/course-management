package com.coursemanagement.service.impl;

import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.rest.dto.MarkAssignmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UserLessonDto;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.service.UserAssociationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;
    private final UserAssociationService userAssociationService;
    private final MarkService markService;
    private final ModelMapper mapper;

    @Override
    public Set<Lesson> getLessonsPerCourse(final Long courseCode) {
        return lessonRepository.findAllByCourseCode(courseCode).stream()
                .map(lessonEntity -> mapper.map(lessonEntity, Lesson.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserLessonDto> getUserLessonsWithContentPerCourse(final Long userId, final Long courseCode) {
        validateUserCourseAccess(userId, courseCode);
        final Set<Lesson> lessonsPerCourse = getLessonsPerCourse(courseCode);
        final Set<Long> lessonIds = lessonsPerCourse.stream()
                .map(Lesson::getId)
                .collect(Collectors.toSet());
        final Map<Long, Set<LessonContent>> lessonContents = lessonContentRepository.findAllByLessonIdIn(lessonIds).stream()
                .collect(Collectors.groupingBy(
                        LessonContent::getLessonId,
                        Collectors.toSet()
                ));
        final Map<Long, BigDecimal> lessonMarks = markService.getAverageLessonMarksForStudentPerCourse(userId, courseCode);

        return lessonsPerCourse.stream()
                .map(toUserLesson(lessonContents, lessonMarks))
                .sorted(Comparator.comparing(UserLessonDto::lessonId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Function<Lesson, UserLessonDto> toUserLesson(final Map<Long, Set<LessonContent>> lessonContents,
                                                                final Map<Long, BigDecimal> lessonMarks) {
        return lesson -> {
            final Long lessonId = lesson.getId();
            final Set<LessonContent> lessonContent = lessonContents.get(lessonId);
            final BigDecimal lessonMarkValue = lessonMarks.get(lessonId);

            return new UserLessonDto(lesson, lessonContent, lessonMarkValue);
        };
    }

    private void validateUserCourseAccess(Long userId, Long courseCode) {
        if (!userAssociationService.isUserAssociatedWithCourse(userId, courseCode)) {
            throw new SystemException("Access to the lesson is limited to associated users only", SystemErrorCode.FORBIDDEN);
        }
    }

    @Override
    public MarkAssignmentResponseDto assignMarkToUserLesson(final MarkAssignmentRequestDto markAssignmentRequestDto) {
        validateLessonMarkAssignment(markAssignmentRequestDto);
        return markService.assignMarkToStudentLesson(markAssignmentRequestDto);
    }

    private void validateLessonMarkAssignment(final MarkAssignmentRequestDto markAssignmentRequestDto) {
        if (!userAssociationService.currentUserHasAccessTo(markAssignmentRequestDto.instructorId())) {
            throw new SystemException("Current user cannot assign mark to lesson", SystemErrorCode.FORBIDDEN);
        }
        if (!userAssociationService.isUserAssociatedWithLesson(markAssignmentRequestDto.studentId(), markAssignmentRequestDto.lessonId())) {
            throw new SystemException("Student is not associated with lesson", SystemErrorCode.FORBIDDEN);
        }
    }
}
