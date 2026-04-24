package org.DPT.shared.auth;

/**
 * Rappresenta i ruoli di accesso al sistema.
 * Questi ruoli mappano direttamente gli utenti MariaDB definiti nello schema SQL
 * e le configurazioni nel file db.properties.
 */
public enum Role {
    LOGIN(0, "Autenticazione di Sistema (Tecnico)"),
    PROPRIETARIO(1, "Proprietario"),
    PT(2, "Personal Trainer"),
    SEGRETERIA(3, "Segreteria"),
    CLIENTE(4, "Cliente");

    private final int id;
    private final String description;

    Role(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Restituisce il prefisso per le chiavi nel file db.properties.
     * Es: Role.PT -> "db.PT"
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
