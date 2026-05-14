package org.dpt.user.management.controller;

import org.dpt.auth.Role;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.management.utils.UserTypeHandler;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.user.receptionist.dao.ReceptionistDAO;

import java.util.EnumMap;
import java.util.Map;

/**
 * Controller specializzato nell'amministrazione delle anagrafiche di sistema.
 * -
 * Agisce come un componente riutilizzabile e "Shared", orchestrando le operazioni 
 * comuni di gestione utenti (PT, Segreteria, Clienti) per i moduli che ne hanno 
 * il privilegio (Owner e Receptionist).
 * -
 * L'architettura del controller si avvale di:
 * <ul>
 *   <li><b>Strategy Map:</b> Una collezione di {@link UserTypeHandler} per eliminare
 *       gli switch nidificati e gestire polimorficamente i diversi ruoli.</li>
 *   <li><b>Cross-DAO Orchestration:</b> Coordina l'accesso a diversi DAO verticali
 *       mantenendo il rispetto del perimetro autorizzativo (canCreateClients).</li>
 * </ul>
 */
public class UserManagementController {

    private final UserManagementUI ui;
    private final PTDAO ptDAO;
    private final ReceptionistDAO receptionistDAO;
    private final ClientDAO clientDAO;
    
    /** Flag di autorizzazione granulare: definisce se l'attore corrente può registrare nuovi clienti. */
    private final boolean canCreateClients;
    
    private static final String UPDATED = "Stato dell'account aggiornato correttamente.";

    /** Registro delle strategie operative suddivise per ruolo. */
    private final Map<Role, UserTypeHandler> userHandlers = new EnumMap<>(Role.class);

    /**
     * Inizializza il controller amministrativo iniettando i DAO necessari.
     */
    public UserManagementController(UserManagementUI ui, 
                                    PTDAO ptDAO, 
                                    ReceptionistDAO receptionistDAO, 
                                    ClientDAO clientDAO,
                                    boolean canCreateClients) {
        this.ui = ui;
        this.ptDAO = ptDAO;
        this.receptionistDAO = receptionistDAO;
        this.clientDAO = clientDAO;
        this.canCreateClients = canCreateClients;
        initializeHandlers();
    }

    /**
     * Configura le strategie di gestione per ogni tipologia di utente supportata.
     * Definisce le implementazioni anonime per le operazioni di I/O e Persistenza.
     */
    private void initializeHandlers() {
        // Gestore per l'anagrafica Personal Trainer
        userHandlers.put(Role.PT, new UserTypeHandler() {
            @Override public void showList() { ui.showUsers(ptDAO.getAll(), Role.PT.getPlural()); }
            @Override public void toggleStatus() {
                ptDAO.updateStatus(ui.askForUserID(), ui.askForNewStatus());
                ui.reportSuccess(UPDATED);
            }
            @Override public void createNew() { ptDAO.insert(ui.askForStaffData()); ui.reportSuccess("Personal Trainer inserito nel sistema."); }
        });

        // Gestore per l'anagrafica Addetti Segreteria
        userHandlers.put(Role.RECEPTIONIST, new UserTypeHandler() {
            @Override public void showList() { ui.showUsers(receptionistDAO.getAll(), Role.RECEPTIONIST.getPlural()); }
            @Override public void toggleStatus() {
                receptionistDAO.updateStatus(ui.askForUserID(), ui.askForNewStatus());
                ui.reportSuccess(UPDATED);
            }
            @Override public void createNew() { receptionistDAO.insert(ui.askForStaffData()); ui.reportSuccess("Addetto segreteria inserito nel sistema."); }
        });

        // Gestore per l'anagrafica Clienti (Atleti)
        userHandlers.put(Role.CLIENT, new UserTypeHandler() {
            @Override public void showList() { ui.showUsers(clientDAO.getAll(), Role.CLIENT.getPlural()); }
            @Override public void toggleStatus() {
                int id = ui.askForUserID();
                if (ui.askForNewStatus()) clientDAO.activate(id);
                else clientDAO.deactivate(id);
                ui.reportSuccess(UPDATED);
            }
            @Override public void createNew() {
                if (canCreateClients) {
                    clientDAO.insert(ui.askForClientData());
                    ui.reportSuccess("Cliente iscritto correttamente.");
                } else {
                    ui.reportError("L'inserimento dei Clienti è riservato esclusivamente alla Segreteria.");
                }
            }
        });
    }

    /**
     * Avvia il workflow interattivo per la selezione del ruolo da amministrare.
     */
    public void manageUsers() {
        boolean back = false;
        while (!back) {
            ui.showUsersMenu();
            int choice = ui.askForChoice();
            switch (choice) {
                case 1 -> manageSpecificUser(Role.PT);
                case 2 -> manageSpecificUser(Role.RECEPTIONIST);
                case 3 -> manageSpecificUser(Role.CLIENT);
                case 0 -> back = true;
                default -> ui.reportError("Selezione non valida.");
            }
        }
    }

    /**
     * Delega l'azione di gestione allo handler specifico per il ruolo scelto.
     */
    private void manageSpecificUser(Role role) {
        UserTypeHandler handler = userHandlers.get(role);
        if (handler == null) return;

        boolean back = false;
        while (!back) {
            ui.showUserActionMenu(role);
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> handler.showList();
                    case 2 -> handler.toggleStatus();
                    case 3 -> handler.createNew();
                    case 0 -> back = true;
                    default -> ui.reportError("Selezione non valida.");
                }
            } catch (Exception e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
