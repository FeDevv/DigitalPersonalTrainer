package org.DPT.users.receptionist.controller;

import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.model.User;

import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Addetto Segreteria.
 */
public interface ReceptionistUI {
    void showHeader(String name);
    void showMainMenu();
    void showUtenzeMenu();
    void showUtenzaActionMenu(String tipo);
    
    int askForChoice();
    
    // Metodi di Input
    UserCreationDTO askForStaffData();
    ClientCreationDTO askForClientData();
    int askForIDUtente();
    boolean askForNewStatus();
    
    // Gestione Assegnazioni
    int askForPTId();
    int askForClientId();

    void showUtenti(List<? extends User> lista, String titolo);

    void reportError(String message);
    void reportSuccess(String message);
    void reportGoodbye();
}
