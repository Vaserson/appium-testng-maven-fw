package org.app.exceptions;

public class EndOfPageException extends FrameworkException {
    public EndOfPageException(String message) {
        super(message);
    }
    public EndOfPageException(String message, Throwable cause) {
        super(message, cause);
    }
}
