package com.coursemanagement.rest;

import com.coursemanagement.annotation.CurrentUser;
import com.coursemanagement.model.File;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.CourseDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseRequestDto;
import com.coursemanagement.rest.dto.StudentEnrollInCourseResponseDto;
import com.coursemanagement.service.CourseService;
import com.coursemanagement.service.HomeworkService;
import com.coursemanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/student")
@RequiredArgsConstructor
public class StudentResource {
    private final CourseService courseService;
    private final StudentService studentService;
    private final HomeworkService homeworkService;

    @PostMapping(value = "/course/enrollments")
    public StudentEnrollInCourseResponseDto enrollInCourse(@RequestBody StudentEnrollInCourseRequestDto studentEnrollInCourseRequestDto) {
        return studentService.enrollStudentInCourses(studentEnrollInCourseRequestDto);
    }

    @PostMapping(value = "/homework/upload")
    public void uploadHomework(@CurrentUser User user,
                               @RequestParam("lesson-id") final Long lessonId,
                               @RequestParam("file") MultipartFile homework) {
        homeworkService.uploadHomework(user.getId(), lessonId, homework);
    }

    @GetMapping(value = "/homework/download/{file-id}")
    public ResponseEntity<byte[]> uploadHomework(@PathVariable("file-id") final Long fileId) {
//        TODO: add checking if a student actually allowed to download the file
        final File homework = homeworkService.downloadHomework(fileId);
        return ResponseEntity.ok()
                .headers(createHeaders(homework))
                .body(homework.getFileContent());

    }

    @GetMapping(value = "{studentId}/courses")
    public Set<CourseDto> getAllCoursesForCurrentUser(@PathVariable(value = "studentId") final Long studentId) {
        return courseService.getAllByUserId(studentId);
    }

    private HttpHeaders createHeaders(final File file) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(file.getFileContent().length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName() + ".txt");
        return headers;
    }
}
