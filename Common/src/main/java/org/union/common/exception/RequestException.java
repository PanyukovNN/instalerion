package org.union.common.exception;

/**
 * Exception while request processing
 */
public class RequestException extends InstalerionException {

    private static final String DEFAULT_MESSAGE = "Ошибка при обработке запроса";

    public RequestException(String message) {
        super(message);
    }

    public RequestException(Exception exception) {
        super(DEFAULT_MESSAGE, exception);
    }
}
