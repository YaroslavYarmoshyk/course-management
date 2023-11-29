package com.coursemanagement.rest.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadHomeworkDto(@NotNull Long lessonId, @NotNull MultipartFile file) {
}
