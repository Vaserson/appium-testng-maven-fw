package org.apidemos.exceptions;

public class ElementNotFoundException extends FrameworkException {
    public ElementNotFoundException(String message) {
        super(message);
    }
    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
