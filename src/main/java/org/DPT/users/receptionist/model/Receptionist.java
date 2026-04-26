package org.DPT.users.receptionist.model;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.model.User;

public class Receptionist extends User {
    public Receptionist(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.RECEPTIONIST, active);
    }
}
