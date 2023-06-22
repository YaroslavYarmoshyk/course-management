package com.coursemanagement.model;

import com.coursemanagement.enumeration.FileType;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class File {
    private Long id;
    private String fileName;
    private FileType fileType;
    private byte[] fileContent;
}
