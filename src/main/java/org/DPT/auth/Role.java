package org.DPT.auth;

/**
 * Rappresenta i ruoli di accesso al sistema.
 * Questi ruoli mappano direttamente gli utenti MariaDB definiti nello schema SQL
 * e le configurazioni nel file db.properties.
 */
public enum Role {
    LOGIN,
    PROPRIETARIO,
    PT,
    SEGRETERIA,
    CLIENTE;

    /**
     * Restituisce il prefisso per le chiavi nel file db.properties.
     * Es: Role.PT -> "db.PT"
     */
    public String getPropertyKey() {
        return "db." + this.name();
    }
}
