package org.app.exceptions;

public class WaitInSecondsException extends FrameworkException {
    public WaitInSecondsException(String message) {
        super(message);
    }
    public WaitInSecondsException(String message, Throwable cause) {
        super(message, cause);
    }
}
