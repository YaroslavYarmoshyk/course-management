package com.coursemanagement.rest;

import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.rest.dto.MarkAssignmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.LESSONS_ENDPOINT;

@RestController
@RequestMapping(LESSONS_ENDPOINT)
@RequiredArgsConstructor
public class LessonResource {
    private final LessonService lessonService;

    @InstructorAccessLevel
    @PostMapping(value = "/assign-mark")
    public MarkAssignmentResponseDto assignMarkToLesson(@Valid @RequestBody final MarkAssignmentRequestDto markAssignmentRequestDto) {
        return lessonService.assignMarkToUserLesson(markAssignmentRequestDto);
    }
}
