package com.panyukovnn.common.exception;

public class RequestException extends InstalerionException {

    private static final String DEFAULT_MESSAGE = "Ошибка при отправке запроса";

    public RequestException(Exception exception) {
        super(DEFAULT_MESSAGE, exception);
    }
}
