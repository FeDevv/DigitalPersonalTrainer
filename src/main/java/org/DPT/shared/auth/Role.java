package org.DPT.shared.auth;

/**
 * Rappresenta i ruoli di accesso al sistema.
 * Questi ruoli mappano direttamente gli utenti MariaDB definiti nello schema SQL
 * e le configurazioni nel file db.properties.
 */
public enum Role {
    LOGIN(0, "Autenticazione di Sistema (Tecnico)", "LOGIN"),
    OWNER(1, "Proprietario", "PROPRIETARIO"),
    PT(2, "Personal Trainer", "PT"),
    RECEPTIONIST(3, "Segreteria", "SEGRETERIA"),
    CLIENT(4, "Cliente", "CLIENTE");

    private final int id;
    private final String description;
    private final String configKey;

    Role(int id, String description, String configKey) {
        this.id = id;
        this.description = description;
        this.configKey = configKey;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Restituisce il prefisso per le chiavi nel file db.properties.
     * Es: Role.OWNER -> "db.PROPRIETARIO"
     */
    public String getPropertyKey() {
        return "db." + this.configKey;
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
