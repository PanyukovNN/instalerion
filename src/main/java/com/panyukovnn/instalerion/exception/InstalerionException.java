package com.panyukovnn.instalerion.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstalerionException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(InstalerionException.class);

    public InstalerionException(String message, Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
        System.out.println();
        cause.printStackTrace();
    }

    public InstalerionException(String message) {
        super(message);
        LOG.error(message);
        System.out.println();
    }
}
