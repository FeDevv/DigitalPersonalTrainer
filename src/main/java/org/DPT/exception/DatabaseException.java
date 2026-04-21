package org.DPT.exception;

/**
 * Eccezione lanciata in caso di errori di persistenza o violazione vincoli DB.
 */
public class DatabaseException extends DPTException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
