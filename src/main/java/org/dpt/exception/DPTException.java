package org.dpt.exception;

/**
 * Base per tutte le eccezioni custom del sistema Digital Personal Trainer.
 * Estende RuntimeException per favorire un codice pulito e ridurre il boilerplate
 * delle TRADUZIONE exceptions, permettendo una gestione centralizzata degli errori.
 */
public abstract class DPTException extends RuntimeException {
    protected DPTException(String message) {
        super(message);
    }

    protected DPTException(String message, Throwable cause) {
        super(message, cause);
    }
}
