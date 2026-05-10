package org.dpt.exception;

/**
 * Eccezione lanciata quando un'entità richiesta (es. PT, Cliente, Macchinario)
 * non viene trovata nel database.
 * Estende DPTException per mantenere la gerarchia di base del sistema.
 */
public class EntityNotFoundException extends DPTException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
