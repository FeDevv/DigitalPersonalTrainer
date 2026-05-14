package org.dpt.user.management.controller;

import org.dpt.auth.Role;
import org.dpt.user.management.dto.ClientCreationDTO;
import org.dpt.user.management.dto.UserCreationDTO;
import org.dpt.domain.user.User;

import java.util.List;

/**
 * Contratto di astrazione per l'interfaccia utente dedicata alla gestione anagrafiche.
 * -
 * Definisce i metodi di I/O necessari per la visualizzazione di liste utenti, 
 * l'acquisizione di dati di registrazione e la modifica dello stato degli account.
 * Garantisce che il {@link UserManagementController} rimanga agnostico rispetto 
 * alla tecnologia di presentazione (CLI/GUI).
 */
public interface UserManagementUI {
    /** Mostra il menu di selezione della tipologia di utente (PT, Segreteria, Clienti). */
    void showUsersMenu();
    
    /** Mostra le azioni disponibili (Lista, Stato, Nuovo) per un ruolo specifico. */
    void showUserActionMenu(Role tipo);
    
    /** Acquisisce la scelta numerica dell'utente. */
    int askForChoice();
    
    /** Acquisisce i dati anagrafici e credenziali per il personale di staff. */
    UserCreationDTO askForStaffData();
    
    /** Acquisisce il set completo di dati per l'iscrizione di un cliente. */
    ClientCreationDTO askForClientData();
    
    /** Richiede l'ID univoco dell'utente su cui operare. */
    int askForUserID();
    
    /** Richiede la conferma per il nuovo stato di attività dell'account. */
    boolean askForNewStatus();

    /** Renderizza in formato tabellare una lista di utenti. */
    void showUsers(List<? extends User> lista, String titolo);

    /** Notifica un errore di validazione o di sistema. */
    void reportError(String message);
    
    /** Notifica il completamento positivo di un'operazione amministrativa. */
    void reportSuccess(String message);
}
