package org.DPT.users.common.controller;

import org.DPT.shared.auth.Role;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.model.User;

import java.util.List;

/**
 * Interfaccia che definisce il contratto UI per la gestione delle utenze.
 * Permette al UserManagementController di operare in modo agnostico rispetto alla View specifica.
 */
public interface UserManagementUI {
    void showUtenzeMenu();
    void showUtenzaActionMenu(Role tipo);
    int askForChoice();
    
    UserCreationDTO askForStaffData();
    ClientCreationDTO askForClientData();
    
    int askForIDUtente();
    boolean askForNewStatus();

    void showUtenti(List<? extends User> lista, String titolo);

    void reportError(String message);
    void reportSuccess(String message);
}
