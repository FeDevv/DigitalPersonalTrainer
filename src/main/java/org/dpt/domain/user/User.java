package org.dpt.domain.user;

import org.dpt.auth.Role;

/**
 * Astrazione fondamentale che rappresenta un utente generico all'interno del sistema.
 * -
 * Questa classe definisce lo schema comune di attributi condivisi da tutti gli attori
 * del sistema (Proprietario, PT, Segreteria, Cliente), riflettendo la struttura delle
 * colonne comuni presenti nelle diverse tabelle dell'anagrafica nel database MariaDB.
 * -
 * Essendo dichiarata abstract, impone l'estensione per ruoli specifici, garantendo
 * che ogni istanza operativa nel sistema abbia un ruolo (Role) e un'identità certa.
 */
public abstract class User {
    /** Identificativo univoco (Primary Key) nel database. */
    protected final int id;
    
    protected final String firstName;
    protected final String lastName;
    
    /** Indirizzo email, utilizzato come identificativo unico per il login (UQ). */
    protected final String email;
    
    /** Il ruolo autorizzativo associato all'utente. */
    protected final Role role;
    
    /** Stato di attivazione dell'account (Soft-delete pattern). */
    protected final boolean active;

    /**
     * Inizializza i campi core dell'utente.
     */
    protected User(int id, String firstName, String lastName, String email, Role role, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }

    /**
     * Restituisce la stringa formattata del nominativo completo (Nome Cognome).
     * @return Stringa concatenata.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
