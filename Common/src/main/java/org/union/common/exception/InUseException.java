package org.union.common.exception;

/**
 * Instance in use exception
 */
public class InUseException extends InstalerionException {

    public InUseException(String message) {
        super(message);
    }

    public InUseException(String message, Exception exception) {
        super(message, exception);
    }
}
