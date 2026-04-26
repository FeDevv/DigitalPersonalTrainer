package org.DPT.users.common.model;

import org.DPT.shared.auth.Role;

/**
 * Base class representing a generic system user.
 * Provides core fields shared across all roles.
 */
public abstract class User {
    protected final int id;
    protected final String firstName;
    protected final String lastName;
    protected final String email;
    protected final Role role;
    protected final boolean active;

    protected User(int id, String firstName, String lastName, String email, Role role, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
