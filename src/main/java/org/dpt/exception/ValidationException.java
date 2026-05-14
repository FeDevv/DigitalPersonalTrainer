package org.dpt.exception;

/**
 * Indica che i dati forniti in input non superano i controlli di integrità o di dominio.
 * -
 * Viene sollevata principalmente dai costruttori compatti dei DTO (Java Records) 
 * o dalle utility di validazione per prevenire la propagazione di stati inconsistenti 
 * verso gli strati profondi dell'applicazione.
 */
public class ValidationException extends DPTException {
    public ValidationException(String message) {
        super(message);
    }
}
