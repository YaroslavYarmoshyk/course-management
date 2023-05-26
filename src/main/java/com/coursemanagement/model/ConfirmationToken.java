package com.coursemanagement.model;

import com.coursemanagement.enumeration.TokenType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ConfirmationToken {
    private Long id;
    private Long userId;
    private TokenType type;
    private String token;
    private LocalDateTime expirationDate;
    private boolean activated;
}
