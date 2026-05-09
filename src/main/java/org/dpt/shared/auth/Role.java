package org.dpt.shared.auth;

/**
 * Rappresenta i ruoli di accesso al sistema.
 * Questi ruoli mappano direttamente gli utenti MariaDB definiti nello schema SQL
 * e le configurazioni nel file db.properties.
 */
public enum Role {
    LOGIN(0, "LOGIN", "LOGIN"),
    OWNER(1, "PROPRIETARIO", "PROPRIETARI"),
    PT(2, "PERSONAL TRAINER", "PERSONAL TRAINER"),
    RECEPTIONIST(3, "ADDETTO SEGRETERIA", "ADDETTI SEGRETERIA"),
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
     * Restituisce il prefisso per le chiavi nel file db.properties.
     * Es: Role.OWNER -> "db.OWNER"
     */
    public String getPropertyKey() {
        return "db." + this.name();
    }

    /**
     * Cerca il ruolo corrispondente a un ID.
     * @param id l'ID inserito.
     * @return la costante Role o null se non trovata.
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
