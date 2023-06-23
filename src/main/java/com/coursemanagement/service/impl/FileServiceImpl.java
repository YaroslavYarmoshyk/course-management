package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.SystemErrorCode;
import com.coursemanagement.exeption.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.repository.FileRepository;
import com.coursemanagement.repository.entity.FileEntity;
import com.coursemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final ModelMapper mapper;

    @Override
    public File getFileById(final Long fileId) {
        return fileRepository.findById(fileId)
                .map(fileEntity -> mapper.map(fileEntity, File.class))
                .orElseThrow(() -> new SystemException("File with id: " + fileId + " not found", SystemErrorCode.BAD_REQUEST));
    }

    @Override
    public File save(final File file) {
        final FileEntity savedFile = fileRepository.save(mapper.map(file, FileEntity.class));
        return mapper.map(savedFile, File.class);
    }
}
