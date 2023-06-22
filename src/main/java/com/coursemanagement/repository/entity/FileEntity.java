package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.FileType;
import com.coursemanagement.enumeration.converter.FileTypeEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "file")
public class FileEntity {
    @Id
    @GeneratedValue(generator = "file_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "file_id_seq",
            sequenceName = "file_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    @Convert(converter = FileTypeEnumConverter.class)
    private FileType fileType;

    @Column(name = "file_content")
    private byte[] fileContent;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final FileEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
