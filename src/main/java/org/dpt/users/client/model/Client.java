package org.dpt.users.client.model;

import org.dpt.shared.auth.Role;
import org.dpt.users.common.model.User;

/**
 * Rappresenta un Cliente.
 * Estende User, raggruppa i dati personali tramite ClientPersonalInfo per ridurre la complessità del costruttore.
 */
public class Client extends User {
    private final ClientPersonalInfo personalInfo;

    public Client(int id, String firstName, String lastName, String email, 
                  ClientPersonalInfo personalInfo, boolean active) {
        super(id, firstName, lastName, email, Role.CLIENT, active);
        this.personalInfo = personalInfo;
    }

    public String getFiscalCode() { return personalInfo.fiscalCode(); }
    public String getAddress() { return personalInfo.address(); }
    public java.time.LocalDate getBirthDate() { return personalInfo.birthDate(); }
}
