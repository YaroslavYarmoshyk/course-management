package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.model.File;
import com.coursemanagement.rest.dto.MarkAssigmentRequestDto;
import com.coursemanagement.rest.dto.MarkAssignmentResponseDto;
import com.coursemanagement.rest.dto.UploadHomeworkDto;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.coursemanagement.util.Constants.LESSONS_ENDPOINT;

@RestController
@RequestMapping(LESSONS_ENDPOINT)
@RequiredArgsConstructor
public class LessonResource {
    private final LessonService lessonService;
    private final HomeworkService homeworkService;


    @InstructorAccessLevel
    @PostMapping(value = "/assign-mark")
    public MarkAssignmentResponseDto assignMarkToLesson(@RequestBody final MarkAssigmentRequestDto markAssigmentRequestDto) {
        return lessonService.assignMarkToUserLesson(markAssigmentRequestDto);
    }

    @PostMapping(value = "/{lesson-id}/homework/upload")
    public void uploadHomework(@CurrentUserId final Long studentId,
                               @PathVariable(value = "lesson-id") final Long lessonId,
                               @RequestParam(value = "file") final MultipartFile homework) {
        homeworkService.uploadHomework(studentId, lessonId, homework);
    }

    @PostMapping(value = "/homework/upload")
    public void uploadHomework1(@CurrentUserId final Long studentId,
                                @ModelAttribute @Valid final UploadHomeworkDto uploadHomeworkDto) {
        homeworkService.uploadHomework(studentId, uploadHomeworkDto.lessonId(), uploadHomeworkDto.file());
    }

    @GetMapping(value = "/homework/download/{file-id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadHomework(@CurrentUserId final Long userId,
                                                   @PathVariable(value = "file-id") final Long fileId) {
        final File homework = homeworkService.downloadHomework(userId, fileId);
        return ResponseEntity.ok(homework.getFileContent());
    }
}
