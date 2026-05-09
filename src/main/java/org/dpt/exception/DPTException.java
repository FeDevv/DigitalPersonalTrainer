package org.dpt.exception;

/**
 * Base per tutte le eccezioni custom del sistema Digital Personal Trainer.
 * Estende RuntimeException per favorire un codice pulito e ridurre il boilerplate
 * delle PROSSIMI PASSI exceptions, permettendo una gestione centralizzata degli errori.
 */
public abstract class DPTException extends RuntimeException {
    public DPTException(String message) {
        super(message);
    }

    public DPTException(String message, Throwable cause) {
        super(message, cause);
    }
}
