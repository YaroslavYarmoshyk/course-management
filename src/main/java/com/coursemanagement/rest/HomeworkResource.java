package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.model.File;
import com.coursemanagement.rest.dto.UploadHomeworkDto;
import com.coursemanagement.service.HomeworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.coursemanagement.util.Constants.HOMEWORK_ENDPOINT;

@RestController
@RequestMapping(value = HOMEWORK_ENDPOINT)
@RequiredArgsConstructor
public class HomeworkResource {
    private final HomeworkService homeworkService;

    @PostMapping(value = "/upload")
    public void uploadHomework1(@CurrentUserId final Long studentId,
                                @ModelAttribute @Valid final UploadHomeworkDto uploadHomeworkDto) {
        homeworkService.uploadHomework(studentId, uploadHomeworkDto.lessonId(), uploadHomeworkDto.file());
    }

    @GetMapping(value = "/download/{file-id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadHomework(@CurrentUserId final Long userId,
                                                   @PathVariable(value = "file-id") final Long fileId) {
        final File homework = homeworkService.downloadHomework(userId, fileId);
        return ResponseEntity.ok(homework.getFileContent());
    }
}
