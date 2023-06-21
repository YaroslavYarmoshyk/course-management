package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.TokenStatus;
import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.enumeration.converter.TokenEnumConverter;
import com.coursemanagement.enumeration.converter.TokenStatusEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "confirmation_token")
public class ConfirmationTokenEntity {
    @Id
    @GeneratedValue(generator = "confirmation_token_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "confirmation_token_id_seq",
            sequenceName = "confirmation_token_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Convert(converter = TokenEnumConverter.class)
    @Column(name = "type")
    private TokenType type;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "status")
    @Convert(converter = TokenStatusEnumConverter.class)
    private TokenStatus status = TokenStatus.NOT_ACTIVATED;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final ConfirmationTokenEntity other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
