package org.DPT.users.login.controller;

import org.DPT.shared.auth.Role;

/**
 * Contratto per l'interfaccia utente del modulo Login.
 * Permette al LogicController di interagire con l'utente in modo agnostico rispetto alla tecnologia (CLI/GUI).
 */
public interface LoginUI {
    void showHeader();
    void showMenu();
    int askForChoice();
    String askForEmail();
    String askForPassword();
    void reportError(String message);
    void reportGoodbye();
}
