package com.coursemanagement.service.impl;

import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.exeption.enumeration.SystemErrorCode;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.LessonContent;
import com.coursemanagement.repository.LessonContentRepository;
import com.coursemanagement.repository.LessonRepository;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.StudentLessonDto;
import com.coursemanagement.service.CourseAssociationService;
import com.coursemanagement.service.LessonAssociationService;
import com.coursemanagement.service.LessonService;
import com.coursemanagement.service.MarkService;
import com.coursemanagement.util.AuthorizationUtil;
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
    private final LessonAssociationService lessonAssociationService;
    private final CourseAssociationService courseAssociationService;
    private final MarkService markService;
    private final ModelMapper mapper;

    @Override
    public Set<Lesson> getLessonsPerCourse(final Long courseCode) {
        return lessonRepository.findAllByCourseCode(courseCode).stream()
                .map(lessonEntity -> mapper.map(lessonEntity, Lesson.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<StudentLessonDto> getStudentLessonsWithContentPerCourse(final Long studentId, final Long courseCode) {
        validateUserCourseAccess(studentId, courseCode);
        final Set<Lesson> lessonsPerCourse = getLessonsPerCourse(courseCode);
        final Set<Long> lessonIds = lessonsPerCourse.stream()
                .map(Lesson::getId)
                .collect(Collectors.toSet());
        final Map<Long, Set<LessonContent>> lessonContents = lessonContentRepository.findAllByLessonIdIn(lessonIds).stream()
                .collect(Collectors.groupingBy(
                        LessonContent::getLessonId,
                        Collectors.toSet()
                ));
        final Map<Long, BigDecimal> lessonMarks = markService.getAverageLessonMarksForStudentPerCourse(studentId, courseCode);

        return lessonsPerCourse.stream()
                .map(toStudentLesson(lessonContents, lessonMarks))
                .sorted(Comparator.comparing(StudentLessonDto::id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Function<Lesson, StudentLessonDto> toStudentLesson(final Map<Long, Set<LessonContent>> lessonContents,
                                                                      final Map<Long, BigDecimal> lessonMarks) {
        return lesson -> {
            final Long lessonId = lesson.getId();
            final Set<LessonContent> lessonContent = lessonContents.get(lessonId);
            final BigDecimal lessonMarkValue = lessonMarks.get(lessonId);

            return new StudentLessonDto(lesson, lessonContent, lessonMarkValue);
        };
    }

    private void validateUserCourseAccess(Long userId, Long courseCode) {
        if (!AuthorizationUtil.isCurrentUserAdminOrInstructor() && !courseAssociationService.isUserAssociatedWithCourse(userId, courseCode)) {
            throw new SystemException("Access to the lesson is limited to associated students only", SystemErrorCode.FORBIDDEN);
        }
    }

    @Override
    public MarkAssignmentResponseDto assignMarkToUserLesson(final Long instructorId, final MarkAssigmentRequestDto markAssigmentRequestDto) {
        validateMarkAssigment(markAssigmentRequestDto.studentId(), markAssigmentRequestDto.lessonId());
        return markService.assignMarkToUserLesson(instructorId, markAssigmentRequestDto);
    }

    private void validateMarkAssigment(final Long studentId, final Long lessonId) {
        if (!lessonAssociationService.isUserAssociatedWithLesson(studentId, lessonId)) {
            throw new SystemException("Student is not associated with lesson", SystemErrorCode.BAD_REQUEST);
        }
    }
}
