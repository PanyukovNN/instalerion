package org.union.common.exception;

/**
 * Exception while try to publish publication
 */
public class PublishingException extends InstalerionException {

    private static final String DEFAULT_MESSAGE = "Ошибка при попытке публикации";

    public PublishingException() {

    }

    public PublishingException(String message) {
        super(message);
    }

    public PublishingException(Exception exception) {
        super(DEFAULT_MESSAGE, exception);
    }
}
