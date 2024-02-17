package com.coursemanagement.enumeration;

import com.coursemanagement.enumeration.converter.DatabaseEnum;
import com.coursemanagement.exception.SystemException;
import com.coursemanagement.exception.enumeration.SystemErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.coursemanagement.util.Constants.MARK_CONVERTER_SCALE;
import static com.coursemanagement.util.Constants.MARK_ROUNDING_MODE;

@Getter
@RequiredArgsConstructor
public enum Mark implements DatabaseEnum {
    POOR(BigDecimal.valueOf(1)),
    BELOW_AVERAGE(BigDecimal.valueOf(2)),
    AVERAGE(BigDecimal.valueOf(3)),
    ABOVE_AVERAGE(BigDecimal.valueOf(4)),
    EXCELLENT(BigDecimal.valueOf(5));

    private final BigDecimal value;

    public static Mark of(final BigDecimal value) {
        return Optional.ofNullable(value)
                .map(Mark::getMarkByValue)
                .orElse(null);
    }

    public static Mark of(final int value) {
        return Arrays.stream(Mark.values())
                .filter(mark -> mark.getValue().intValue() == value)
                .findFirst()
                .orElseThrow(() -> new SystemException("Cannot parse " + value + " to mark", SystemErrorCode.INTERNAL_SERVER_ERROR));
    }

    private static Mark getMarkByValue(final BigDecimal markValue) {
        final BigDecimal roundedValue = markValue.setScale(MARK_CONVERTER_SCALE, MARK_ROUNDING_MODE);
        return Arrays.stream(Mark.values())
                .filter(mark -> Objects.equals(mark.getValue(), roundedValue))
                .findAny()
                .orElseThrow(() -> new SystemException("Cannot parse " + roundedValue + " to mark", SystemErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Override
    public BigDecimal toDbValue() {
        return value;
    }
}
