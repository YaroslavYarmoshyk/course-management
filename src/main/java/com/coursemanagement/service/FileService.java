package com.coursemanagement.service;

import com.coursemanagement.model.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File getFileById(final Long fileId);

    File save(final File file);

    File createFile(final MultipartFile multipartFile);
}
