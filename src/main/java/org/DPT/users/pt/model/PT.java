package org.DPT.users.pt.model;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.model.User;

public class PT extends User {
    public PT(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.PT, active);
    }
}
