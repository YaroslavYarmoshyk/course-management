package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.TokenType;
import com.coursemanagement.enumeration.repository.DatabaseEnumConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "userEntity")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Convert(converter = DatabaseEnumConverter.class)
    @Column(name = "type")
    private TokenType type;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "activated")
    private Boolean activated;

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
