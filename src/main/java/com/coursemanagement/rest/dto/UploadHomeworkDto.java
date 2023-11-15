package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record UploadHomeworkDto(@NotBlank Long lessonId, @NotBlank MultipartFile file) {
}
