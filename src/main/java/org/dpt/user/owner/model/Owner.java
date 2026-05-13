package org.dpt.user.owner.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Rappresenta il proprietario
 * Estende User, non implementa nulla di 'nuovo'
 */
public class Owner extends User {
    public Owner(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email, Role.OWNER, true);
    }
}
