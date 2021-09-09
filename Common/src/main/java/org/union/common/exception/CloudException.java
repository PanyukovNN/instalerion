package org.union.common.exception;

/**
 * Cloud exception
 */
public class CloudException extends InstalerionException {

    public CloudException(String message) {
        super(message);
    }

    public CloudException(String message, Exception exception) {
        super(message, exception);
    }
}
