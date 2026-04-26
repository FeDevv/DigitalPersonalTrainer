package org.DPT.users.owner.model;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.model.User;

public class Owner extends User {
    public Owner(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email, Role.OWNER, true);
    }
}
