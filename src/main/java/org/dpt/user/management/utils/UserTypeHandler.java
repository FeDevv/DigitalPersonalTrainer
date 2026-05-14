package org.dpt.user.management.utils;

/**
 * Interfaccia di strategia per la gestione polimorfica delle tipologie di utenza.
 * -
 * Implementa il pattern "Strategy" per isolare le operazioni di amministrazione 
 * (Visualizzazione, Switch di stato, Creazione) in base al ruolo specifico dell'utente. 
 * Questo approccio permette al {@link org.dpt.user.management.controller.UserManagementController} 
 * di gestire diverse entità (PT, Clienti, Receptionist) in modo uniforme, semplificando 
 * l'aggiunta di nuovi ruoli senza alterare la logica di navigazione dei menu.
 */
public interface UserTypeHandler {
    /** Renderizza la lista completa degli utenti appartenenti al ruolo gestito. */
    void showList();
    
    /** Coordina la procedura di attivazione o disattivazione (Soft-delete) di un account. */
    void toggleStatus();
    
    /** Avvia il workflow di creazione e persistenza di una nuova utenza. */
    void createNew();
}
