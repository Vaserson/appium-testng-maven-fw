package org.app.exceptions;

public class UnsupportedPlatformException extends FrameworkException {
    public UnsupportedPlatformException(String message) {
        super(message);
    }
    public UnsupportedPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}
