package org.DPT.users.pt.model;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.model.User;

/**
 * Rappresenta un personal trainer
 * Estende User, non implementa nulla di 'nuovo'
 */
public class PT extends User {
    public PT(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.PT, active);
    }
}
