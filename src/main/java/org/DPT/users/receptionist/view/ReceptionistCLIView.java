package org.DPT.users.receptionist.view;

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

    public void displayUtenzaActionMenu(String tipo) {
        displaySectionTitle("Azioni " + tipo);
        displayLine("1. Visualizza lista " + tipo);
        displayLine("2. Attiva/Disattiva " + tipo);
        displayLine("3. Inserisci nuovo " + tipo);
        displayLine("0. Torna indietro");
    }

    public void displayUtenti(List<? extends User> lista, String titolo) {
        displaySectionTitle("Elenco " + titolo);
        
        String[] headers = {"ID", "NOMINATIVO COMPLETO", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (User u : lista) {
            rows.add(new String[]{
                String.valueOf(u.getId()),
                u.getFirstName() + " " + u.getLastName(),
                u.isActive() ? "ATTIVO" : "DISATTIVO"
            });
        }
        
        renderTable(headers, rows, new int[]{5, 30, 10});
    }

    public void displayGoodbye() {
        displayLine("\n Logout effettuato. Arrivederci!");
    }
}
