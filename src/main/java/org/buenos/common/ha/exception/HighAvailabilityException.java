package org.buenos.common.ha.exception;

public class HighAvailabilityException extends RuntimeException {

    public HighAvailabilityException(String errorCode) {
        super(errorCode);
    }

    public HighAvailabilityException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public HighAvailabilityException(Throwable cause) {
        super(cause);
    }
}
