package org.dpt.user.receptionist.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Rappresenta un addetto di segreteria
 * Estende User, non implementa nulla di 'nuovo'
 */
public class Receptionist extends User {
    public Receptionist(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.RECEPTIONIST, active);
    }
}
