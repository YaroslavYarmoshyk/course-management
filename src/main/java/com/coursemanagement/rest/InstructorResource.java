package com.coursemanagement.rest;

import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.rest.dto.UserLessonMarkRequestDto;
import com.coursemanagement.rest.dto.UserLessonMarkResponseDto;
import com.coursemanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/instructor")
@InstructorAccessLevel
@RequiredArgsConstructor
public class InstructorResource {
    private final LessonService lessonService;

    @PostMapping(value = "/mark-lesson")
    public UserLessonMarkResponseDto markLesson(@RequestBody UserLessonMarkRequestDto userLessonMarkRequestDto) {
        return new UserLessonMarkResponseDto(lessonService.markLesson(userLessonMarkRequestDto));
    }
}
