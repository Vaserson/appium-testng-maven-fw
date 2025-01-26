package org.app.exceptions;

public class ImageFileNotFoundException extends FrameworkException {
    public ImageFileNotFoundException(String message) {
        super(message);
    }
    public ImageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
