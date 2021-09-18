package org.union.common.exception;

import lombok.NoArgsConstructor;

/**
 * Exception when requests are too often
 */
public class TooOftenRequestException extends RequestException {

    public TooOftenRequestException() {
    }

    public TooOftenRequestException(String message) {
        super(message);
    }

    public TooOftenRequestException(Exception exception) {
        super(exception);
    }
}
