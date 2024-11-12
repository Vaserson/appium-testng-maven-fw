package org.apidemos.exceptions;

public class NoElementOnAllowedPartException extends FrameworkException {
    public NoElementOnAllowedPartException(String message) {
        super(message);
    }
    public NoElementOnAllowedPartException(String message, Throwable cause) {
        super(message, cause);
    }
}
