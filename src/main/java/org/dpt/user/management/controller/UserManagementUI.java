package org.dpt.user.management.controller;

import org.dpt.auth.Role;
import org.dpt.user.management.dto.ClientCreationDTO;
import org.dpt.user.management.dto.UserCreationDTO;
import org.dpt.domain.user.User;

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
