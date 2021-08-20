package org.union.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Web page not found exception
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PageNotFoundException extends InstalerionException {

    public PageNotFoundException(String message) {
        super(message);
    }

    public PageNotFoundException(String message, Exception exception) {
        super(message, exception);
    }
}
