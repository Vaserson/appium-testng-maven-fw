package org.apidemos.exceptions;

public class SwipeLimitExceededException extends FrameworkException {
    public SwipeLimitExceededException(String message) {
        super(message);
    }
    public SwipeLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
