package com.coursemanagement.service.impl;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.model.File;
import com.coursemanagement.repository.FileRepository;
import com.coursemanagement.repository.entity.FileEntity;
import com.coursemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

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

    @Override
    public File createFile(final MultipartFile multipartFile) {
        try {
            final FileType fileType = Optional.ofNullable(multipartFile)
                    .map(MultipartFile::getContentType)
                    .map(FileType::of)
                    .orElseThrow(() -> new SystemException("Input file cannot be null", SystemErrorCode.BAD_REQUEST));
            final File file = new File()
                    .setFileName(FilenameUtils.removeExtension(multipartFile.getOriginalFilename()))
                    .setFileType(fileType)
                    .setFileContent(multipartFile.getBytes());
            return save(file);
        } catch (final IOException e) {
            throw new SystemException(e.getMessage(), SystemErrorCode.BAD_REQUEST);
        }
    }
}
