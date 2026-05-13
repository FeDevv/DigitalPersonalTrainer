package org.dpt.user.management.controller;

import org.dpt.auth.Role;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.management.utils.UserTypeHandler;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.user.receptionist.dao.ReceptionistDAO;

import java.util.EnumMap;
import java.util.Map;

/**
 * Controller condiviso per la gestione delle anagrafiche utenti.
 * Centralizza la logica di visualizzazione, attivazione/disattivazione e creazione
 * per evitare duplicazioni tra i moduli Owner e Receptionist.
 */
public class UserManagementController {

    private final UserManagementUI ui;
    private final PTDAO ptDAO;
    private final ReceptionistDAO receptionistDAO;
    private final ClientDAO clientDAO;
    private final boolean canCreateClients;
    private static final String UPDATED = "Stato utente aggiornato.";

    private final Map<Role, UserTypeHandler> userHandlers = new EnumMap<>(Role.class);

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

    private void initializeHandlers() {
        // Gestore per i Personal Trainer
        userHandlers.put(Role.PT, new UserTypeHandler() {
            @Override public void showList() { ui.showUsers(ptDAO.getAll(), Role.PT.getPlural()); }
            @Override public void toggleStatus() {
                ptDAO.updateStatus(ui.askForUserID(), ui.askForNewStatus());
                ui.reportSuccess(UPDATED);
            }
            @Override public void createNew() { ptDAO.insert(ui.askForStaffData()); ui.reportSuccess("Personal Trainer inserito."); }
        });

        // Gestore per gli Addetti Segreteria
        userHandlers.put(Role.RECEPTIONIST, new UserTypeHandler() {
            @Override public void showList() { ui.showUsers(receptionistDAO.getAll(), Role.RECEPTIONIST.getPlural()); }
            @Override public void toggleStatus() {
                receptionistDAO.updateStatus(ui.askForUserID(), ui.askForNewStatus());
                ui.reportSuccess(UPDATED);
            }
            @Override public void createNew() { receptionistDAO.insert(ui.askForStaffData()); ui.reportSuccess("Addetto segreteria inserito."); }
        });

        // Gestore per i Clienti
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
                    ui.reportSuccess("Cliente inserito correttamente.");
                } else {
                    ui.reportError("I Clienti possono essere inseriti solo dalla Segreteria.");
                }
            }
        });
    }

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
                default -> ui.reportError("Scelta non valida.");
            }
        }
    }

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
                    default -> ui.reportError("Scelta non valida.");
                }
            } catch (Exception e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
