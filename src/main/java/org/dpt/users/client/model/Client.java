package org.dpt.users.client.model;

import org.dpt.shared.auth.Role;
import org.dpt.users.common.model.User;
import java.time.LocalDate;

/**
 * Rappresenta un Cliente
 * Estende User, ha in più dati anagrafici quali codice fiscale, indirizzo e data di nascita.
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
    //metodi tenuti per completezza e futura espansione
    public String getAddress() { return address; }
    public LocalDate getBirthDate() { return birthDate; }
}
