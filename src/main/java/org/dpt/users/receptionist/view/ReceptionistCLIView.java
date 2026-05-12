package org.dpt.users.receptionist.view;

import org.dpt.shared.auth.Role;
import org.dpt.shared.ui.BaseCLIView;

@SuppressWarnings("java:S106")
public class ReceptionistCLIView extends BaseCLIView {

    public void displayReceptionistHeader(String name) {
        displayHeader("PANNELLO SEGRETERIA - Benvenuto/a " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Gestione UTENZE (Staff/Clienti)");
        displayLine("2. Nuova Assegnazione PT-CLIENTE");
        displayLine("0. Logout");
    }

    public void displayUsersMenu() {
        displaySectionTitle("Gestione UTENZE");
        displayLine("1. Gestione PERSONAL TRAINER");
        displayLine("2. Gestione ADDETTI SEGRETERIA");
        displayLine("3. Gestione CLIENTI");
        displayLine("0. Torna indietro");
    }

    public void displayUserActionMenu(Role role) {
        displaySectionTitle("Azioni " + role);
        displayLine("1. Visualizza lista " + role.getPlural());
        displayLine("2. Attiva/Disattiva " + role.getSingular());
        displayLine("3. Inserisci nuovo " + role.getSingular());
        displayLine("0. Torna indietro");
    }

    public void displayGoodbye() {
        displayLine("\n Logout effettuato. Arrivederci!");
    }
}
