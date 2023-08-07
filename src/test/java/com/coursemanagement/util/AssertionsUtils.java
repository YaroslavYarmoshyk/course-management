package com.coursemanagement.util;

import org.junit.function.ThrowingRunnable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public final class AssertionsUtils {

    public static <T extends Throwable> void assertThrowsWithMessage(final ThrowingRunnable throwingRunnable,
                                                                     final Class<T> expectedError,
                                                                     final String expectedMessage) {
        final Throwable throwable = assertThrows(expectedError, throwingRunnable);
        assertEquals(expectedMessage, throwable.getMessage());
    }
}
