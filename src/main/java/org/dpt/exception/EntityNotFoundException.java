package org.dpt.exception;

/**
 * Segnala l'impossibilità di reperire un'entità specifica richiesta tramite query.
 * -
 * Lanciata dai DAO quando una ricerca per identificativo univoco (es. ID Cliente 
 * o Codice Esercizio) restituisce un insieme vuoto, violando le aspettative 
 * funzionali del modulo chiamante.
 */
public class EntityNotFoundException extends DPTException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
