package org.dpt.users.common.controller;

import org.dpt.shared.auth.Role;
import org.dpt.users.common.dto.ClientCreationDTO;
import org.dpt.users.common.dto.UserCreationDTO;
import org.dpt.users.common.model.User;

import java.util.List;

/**
 * Interfaccia che definisce il contratto UI per la gestione delle utenze.
 * Permette al UserManagementController di operare in modo agnostico rispetto alla View specifica.
 */
public interface UserManagementUI {
    void showUsersMenu();
    void showUserActionMenu(Role tipo);
    int askForChoice();
    
    UserCreationDTO askForStaffData();
    ClientCreationDTO askForClientData();
    
    int askForUserID();
    boolean askForNewStatus();

    void showUsers(List<? extends User> lista, String titolo);

    void reportError(String message);
    void reportSuccess(String message);
}
