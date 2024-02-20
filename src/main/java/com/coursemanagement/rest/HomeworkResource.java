package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUserId;
import com.coursemanagement.annotation.InstructorAccessLevel;
import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.model.File;
import com.coursemanagement.rest.dto.UploadHomeworkDto;
import com.coursemanagement.service.HomeworkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "/download/{file-id}")
    public ResponseEntity<byte[]> downloadHomework(@CurrentUserId final Long userId,
                                                   @PathVariable(value = "file-id") final Long fileId) {
        final File homework = homeworkService.downloadHomework(userId, fileId);
        final HttpHeaders headers = getHttpHeaders(homework);
        return new ResponseEntity<>(homework.getFileContent(), headers, HttpStatus.OK);
    }

    @InstructorAccessLevel
    @GetMapping(value = "/download-solution", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadStudentSolution(@RequestParam(value = "student-id") final Long studentId,
                                                          @RequestParam(value = "lesson-id") final Long lessonId) {
        final File homework = homeworkService.downloadStudentSolution(studentId, lessonId);
        final HttpHeaders headers = getHttpHeaders(homework, "Solution for lesson %d by student %d.%s"
                .formatted(lessonId, studentId, homework.getFileType().getExtension()));
        return new ResponseEntity<>(homework.getFileContent(), headers, HttpStatus.OK);
    }

    private static HttpHeaders getHttpHeaders(final File homework) {
        return getHttpHeaders(homework, getFileName(homework.getFileName(), homework.getFileType()));
    }

    private static HttpHeaders getHttpHeaders(final File homework, final String fileName) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(homework.getFileContent().length);
        return headers;
    }

    private static String getFileName(final String fileName, final FileType fileType) {
        return "%s - %s.%s".formatted(fileName, "homework", fileType.getExtension());
    }
}
