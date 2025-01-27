package org.app.exceptions;

public class JsonLocatorNotFoundException extends FrameworkException {
    public JsonLocatorNotFoundException(String message) {
        super(message);
    }
    public JsonLocatorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
