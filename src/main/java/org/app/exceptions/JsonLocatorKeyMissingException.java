package org.app.exceptions;

public class JsonLocatorKeyMissingException extends FrameworkException {
    public JsonLocatorKeyMissingException(String message) {
        super(message);
    }
    public JsonLocatorKeyMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
