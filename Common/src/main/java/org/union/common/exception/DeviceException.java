package org.union.common.exception;

/**
 * Device exception
 */
public class DeviceException extends InstalerionException {

    public DeviceException(String message) {
        super(message);
    }

    public DeviceException(String message, Exception exception) {
        super(message, exception);
    }
}
