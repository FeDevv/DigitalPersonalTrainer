package org.DPT.users.receptionist.view;

import org.DPT.shared.ui.BaseCLIView;
import org.DPT.users.common.model.User;

import java.util.List;

public class ReceptionistCLIView extends BaseCLIView {

    public void displayReceptionistHeader(String name) {
        displayHeader("Pannello Segreteria - Benvenuto/a " + name);
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
        if (lista.isEmpty()) {
            displayLine("Nessun utente presente in questa categoria.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-10s%n", "ID", "Nome Completo", "Stato");
        displayLine("--------------------------------------------------");
        for (User u : lista) {
            System.out.printf("%-5d | %-25s | %-10s%n", u.getId(), u.getFirstName() + " " + u.getLastName(), u.isActive() ? "ATTIVO" : "DISATTIVO");
        }
    }

    public void displayGoodbye() {
        displayLine("\nLogout effettuato. Arrivederci!");
    }
}
