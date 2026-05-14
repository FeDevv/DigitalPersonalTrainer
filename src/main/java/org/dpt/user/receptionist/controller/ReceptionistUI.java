package org.dpt.user.receptionist.controller;

import org.dpt.user.management.controller.UserManagementUI;

/**
 * Contratto di interfaccia per l'astrazione della UI del modulo Segreteria.
 * -
 * Definisce i metodi necessari per l'interazione con l'operatore, rendendo il 
 * {@link ReceptionistLogicController} agnostico rispetto alla tecnologia di 
 * rendering (CLI o futura GUI). 
 * Estende {@link UserManagementUI} per includere le funzionalità di gestione 
 * anagrafica condivise.
 */
public interface ReceptionistUI extends UserManagementUI {
    /** Mostra l'intestazione del modulo. */
    void showHeader(String name);

    /** Mostra il menu principale della segreteria. */
    void showMainMenu();
    
    /** Richiede l'ID del Personal Trainer per l'assegnazione. */
    int askForPTId();

    /** Richiede l'ID del Cliente per l'assegnazione. */
    int askForClientId();

    /** Segnala l'uscita dal sistema. */
    void reportGoodbye();
}
