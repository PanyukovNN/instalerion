package org.union.common.exception;

/**
 * Exception when object is in use
 */
public class InUseException extends InstalerionException {

    public InUseException(String message) {
        super(message);
    }

    public InUseException(String message, Exception exception) {
        super(message, exception);
    }
}
