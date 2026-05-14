package org.dpt.exception;

/**
 * Segnala anomalie critiche occorse durante l'interazione con il database MariaDB.
 * -
 * Viene utilizzata per incapsulare errori JDBC ({@link java.sql.SQLException}),
 * violazioni di vincoli di integrità referenziale o fallimenti nelle operazioni 
 * di commit/rollback delle transazioni.
 */
public class DatabaseException extends DPTException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
