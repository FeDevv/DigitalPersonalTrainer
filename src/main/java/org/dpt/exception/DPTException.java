package org.dpt.exception;

/**
 * Radice della gerarchia delle eccezioni dell'applicazione Digital Personal Trainer.
 * -
 * Estende {@link RuntimeException} seguendo la filosofia delle "Unchecked Exceptions". 
 * Questa scelta architetturale permette di ridurre il boilerplate del codice (evitando 
 * clausole 'throws' ridondanti) e facilita la gestione centralizzata degli errori 
 * all'interno del Template Method definito nella classe {@link org.dpt.shared.mvc.AbstractLogicController}.
 */
public abstract class DPTException extends RuntimeException {
    /**
     * Costruisce un'eccezione con un messaggio descrittivo.
     * @param message Dettaglio dell'errore da visualizzare all'utente o nei log.
     */
    protected DPTException(String message) {
        super(message);
    }

    /**
     * Costruisce un'eccezione incapsulando la causa originale (Exception Wrapping).
     * @param message Dettaglio dell'anomalia.
     * @param cause L'eccezione originale che ha scatenato il fallimento (es. SQLException).
     */
    protected DPTException(String message, Throwable cause) {
        super(message, cause);
    }
}
