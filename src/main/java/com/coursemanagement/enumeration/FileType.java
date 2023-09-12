package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public enum FileType implements DatabaseEnum {
    TEXT(1), IMAGE(2), AUDIO(3), VIDEO(4);

    private final Integer dbAlias;

    @Override
    public Integer toDbValue() {
        return dbAlias;
    }

    public static FileType of(final String contentType) {
        return Optional.ofNullable(contentType)
                .map(FileType::mapToFileType)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content type: " + contentType));
    }

    private static FileType mapToFileType(final String contentType) {
        return switch (contentType) {
            case "text/plain", "text/html", "application/xml", "application/json", "text/csv" -> FileType.TEXT;
            case "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp" -> FileType.IMAGE;
            case "audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg", "audio/aac" -> FileType.AUDIO;
            case "video/mp4", "video/x-msvideo", "video/quicktime", "video/mpeg" -> FileType.VIDEO;
            default -> throw new IllegalArgumentException("Invalid content type: " + contentType);
        };
    }
}
