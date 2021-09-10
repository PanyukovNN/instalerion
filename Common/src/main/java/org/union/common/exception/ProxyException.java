package org.union.common.exception;

/**
 * Proxy server exception
 */
public class ProxyException extends InstalerionException {

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Exception exception) {
        super(message, exception);
    }
}
