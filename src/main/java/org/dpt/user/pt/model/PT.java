package org.dpt.user.pt.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Rappresenta un personal trainer
 * Estende User, non implementa nulla di 'nuovo'
 */
public class PT extends User {
    public PT(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.PT, active);
    }
}
