package org.apidemos.exceptions;

public class AppNotFoundException extends FrameworkException {
    public AppNotFoundException(String message) {
        super(message);
    }
    public AppNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
