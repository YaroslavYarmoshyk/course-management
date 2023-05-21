package com.coursemanagement.enumeration;

import com.coursemanagement.exeption.SystemException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public enum Mark {
    POOR(BigDecimal.valueOf(1)),
    BELOW_AVERAGE(BigDecimal.valueOf(2)),
    AVERAGE(BigDecimal.valueOf(3)),
    ABOVE_AVERAGE(BigDecimal.valueOf(4)),
    EXCELLENT(BigDecimal.valueOf(5));

    @Getter
    private final BigDecimal value;

    public static Mark of(final Integer value) {
        return Arrays.stream(Mark.values())
                .filter(mark -> Objects.equals(mark.getValue(), BigDecimal.valueOf(value)))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot parse " + value + " to mark", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
