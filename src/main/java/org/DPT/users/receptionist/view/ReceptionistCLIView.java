package org.DPT.users.receptionist.view;

import org.DPT.shared.auth.Role;
import org.DPT.shared.ui.BaseCLIView;
import org.DPT.users.common.model.User;

import java.util.ArrayList;
import java.util.List;

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

    public void displayUtenzeMenu() {
        displaySectionTitle("Gestione UTENZE");
        displayLine("1. Gestione PERSONAL TRAINER");
        displayLine("2. Gestione ADDETTI SEGRETERIA");
        displayLine("3. Gestione CLIENTI");
        displayLine("0. Torna indietro");
    }

    public void displayUtenzaActionMenu(Role role) {
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
