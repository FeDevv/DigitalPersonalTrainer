package org.dpt.user.receptionist.controller;

import org.dpt.user.management.controller.UserManagementUI;

/**
 * Contratto per l'interfaccia utente del modulo Addetto Segreteria.
 * Estende UserManagementUI per la gestione delle anagrafiche.
 */
public interface ReceptionistUI extends UserManagementUI {
    void showHeader(String name);
    void showMainMenu();
    
    // Gestione Assegnazioni (specifico per Receptionist)
    int askForPTId();
    int askForClientId();

    void reportGoodbye();
}
