package org.apidemos.exceptions;

public class URLException extends RuntimeException {
    public URLException(String message) {
        super(message);
    }
    public URLException(String message, Throwable cause) {
        super(message, cause);
    }
}
