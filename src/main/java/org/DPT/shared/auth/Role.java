package org.DPT.shared.auth;

/**
 * Rappresenta i ruoli di accesso al sistema.
 * Questi ruoli mappano direttamente gli utenti MariaDB definiti nello schema SQL
 * e le configurazioni nel file db.properties.
 */
public enum Role {
    LOGIN(0, "Autenticazione di Sistema"),
    OWNER(1, "PROPRIETARIO"),
    PT(2, "PERSONAL TRAINER"),
    RECEPTIONIST(3, "SEGRETERIA"),
    CLIENT(4, "CLIENTE");

    private final int id;
    private final String translation;

    Role(int id, String translation) {
        this.id = id;
        this.translation = translation;
    }

    public int getId() {
        return id;
    }

    public String getTranslation() {
        return translation;
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
