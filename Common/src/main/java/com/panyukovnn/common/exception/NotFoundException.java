package com.panyukovnn.common.exception;

public class NotFoundException extends InstalerionException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Exception exception) {
        super(message, exception);
    }
}
