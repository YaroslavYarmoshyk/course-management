package com.coursemanagement.service;

import com.coursemanagement.model.File;

public interface FileService {

    File getFileById(final Long fileId);

    File save(final File file);
}
