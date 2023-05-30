package com.coursemanagement.repository.entity;

import com.coursemanagement.enumeration.TokenType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private TokenType type;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "activated")
    private Boolean activated;
}
