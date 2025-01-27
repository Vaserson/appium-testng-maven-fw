package org.app.exceptions;

public class UnsupportedLocatorTypeException extends FrameworkException {
    public UnsupportedLocatorTypeException(String message) {
        super(message);
    }
    public UnsupportedLocatorTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
