package org.DPT.exception;

/**
 * Eccezione lanciata quando un'entità richiesta (es. PT, Cliente, Macchinario)
 * non viene trovata nel database.
 * Estende DatabaseException per mantenere la gerarchia esistente.
 */
public class EntityNotFoundException extends DatabaseException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
