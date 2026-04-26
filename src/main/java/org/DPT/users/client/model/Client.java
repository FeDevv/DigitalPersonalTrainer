package org.DPT.users.client.model;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.model.User;
import java.time.LocalDate;

/**
 * Represents a gym member (Client).
 * Extends User with personal anatomical and registration data.
 */
public class Client extends User {
    private final String fiscalCode;
    private final String address;
    private final LocalDate birthDate;

    public Client(int id, String firstName, String lastName, String email, 
                  String fiscalCode, String address, LocalDate birthDate, boolean active) {
        super(id, firstName, lastName, email, Role.CLIENT, active);
        this.fiscalCode = fiscalCode;
        this.address = address;
        this.birthDate = birthDate;
    }

    public String getFiscalCode() { return fiscalCode; }
    public String getAddress() { return address; }
    public LocalDate getBirthDate() { return birthDate; }
}
