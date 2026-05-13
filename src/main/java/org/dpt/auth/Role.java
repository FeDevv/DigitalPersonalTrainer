package org.dpt.auth;

/**
 * Definizione dei ruoli di accesso e dei livelli di privilegio del sistema.
 * -
 * Questa enumerazione costituisce la mappatura semantica tra la logica applicativa 
 * e l'infrastruttura di persistenza. Ogni costante corrisponde a un utente MariaDB 
 * specifico e a una sezione del file db.properties, permettendo al DBConnectionManager 
 * di implementare lo switching dinamico della connessione (RBAC).
 */
public enum Role {
    /** Ruolo tecnico per la fase di handshake (sola lettura credenziali). */
    LOGIN(0, "LOGIN", "LOGIN"),
    
    /** Gestore totale del sistema, personale e catalogo. */
    OWNER(1, "PROPRIETARIO", "PROPRIETARI"),
    
    /** Responsabile tecnico della gestione atleti e schede. */
    PT(2, "PERSONAL TRAINER", "PERSONAL TRAINER"),
    
    /** Supporto amministrativo per anagrafiche e assegnazioni. */
    RECEPTIONIST(3, "ADDETTO SEGRETERIA", "ADDETTI SEGRETERIA"),
    
    /** Utente finale focalizzato sull'esecuzione dei workout. */
    CLIENT(4, "CLIENTE", "CLIENTI");

    private final int id;
    private final String singular;
    private final String plural;

    Role(int id, String singular, String plural) {
        this.id = id;
        this.singular = singular;
        this.plural = plural;
    }

    public int getId() {
        return id;
    }

    public String getSingular() {
        return singular;
    }

    public String getPlural() {
        return plural;
    }

    /**
     * Calcola la chiave prefissa per recuperare le credenziali dal file di configurazione.
     * @return Stringa nel formato "db.[NOME_RUOLO]" (es: "db.OWNER").
     */
    public String getPropertyKey() {
        return "db." + this.name();
    }

    /**
     * Risolve un ID numerico nel corrispondente oggetto Role.
     * Utilizzato principalmente nei menu di selezione iniziale o nel mapping dei risultati SQL.
     * 
     * @param id L'identificativo numerico del ruolo.
     * @return L'istanza Role associata o null se l'ID non è mappato.
     */
    public static Role getRoleFromId(int id) {
        for (Role role : Role.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        return null;
    }
}
