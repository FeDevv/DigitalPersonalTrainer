package org.dpt.exception;

import java.sql.SQLException;

/**
 * Segnala anomalie critiche occorse durante l'interazione con il database MariaDB.
 * -
 * Implementa una logica di "Auto-Extraction" per i messaggi d'errore provenienti 
 * dai trigger e dalle stored procedure (SQLState 45000). Se il database lancia 
 * un errore di business logic, questa eccezione ignora il messaggio generico 
 * del DAO per presentare all'utente la motivazione specifica definita nello schema SQL,
 * mantenendo i metadati tecnici del driver (es. conn=...).
 */
public class DatabaseException extends DPTException {
    
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(extractMessage(message, cause), cause);
    }

    /**
     * Analizza la causa dell'errore. Se si tratta di un errore di integrità 
     * definito da noi (SQLState 45000), estrae il messaggio originale del driver.
     */
    private static String extractMessage(String message, Throwable cause) {
        if (cause instanceof SQLException sqlEx && "45000".equals(sqlEx.getSQLState())) {
            return sqlEx.getMessage();
        }
        return message;
    }
}
