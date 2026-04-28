package org.DPT.users.owner.view;

import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.ui.BaseCLIView;
import org.DPT.users.common.model.User;

import java.util.List;

public class OwnerCLIView extends BaseCLIView {

    public void displayOwnerHeader(String name) {
        displayHeader("Pannello Proprietario - Benvenuto " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Gestione Macchinari");
        displayLine("2. Gestione Esercizi");
        displayLine("3. Gestione Utenze (Staff/Clienti)");
        displayLine("0. Logout");
    }

    public void displayMacchinariMenu() {
        displaySectionTitle("Gestione Macchinari");
        displayLine("1. Visualizza tutti i macchinari");
        displayLine("2. Inserisci nuovo macchinario");
        displayLine("3. Attiva/Disattiva macchinario");
        displayLine("0. Torna indietro");
    }

    public void displayEserciziMenu() {
        displaySectionTitle("Gestione Esercizi");
        displayLine("1. Visualizza tutti gli esercizi");
        displayLine("2. Inserisci nuovo esercizio");
        displayLine("3. Attiva/Disattiva esercizio");
        displayLine("0. Torna indietro");
    }

    public void displayUtenzeMenu() {
        displaySectionTitle("Gestione Utenze");
        displayLine("1. Gestione Personal Trainer");
        displayLine("2. Gestione Addetti Segreteria");
        displayLine("3. Gestione Clienti");
        displayLine("0. Torna indietro");
    }

    public void displayUtenzaActionMenu(String tipo) {
        displaySectionTitle("Azioni " + tipo);
        displayLine("1. Visualizza lista " + tipo);
        displayLine("2. Inserisci nuovo " + tipo);
        displayLine("3. Attiva/Disattiva " + tipo);
        displayLine("0. Torna indietro");
    }

    public void displayMacchinari(List<Machine> lista) {
        displaySectionTitle("Elenco Macchinari");
        if (lista.isEmpty()) {
            displayLine("Nessun macchinario presente.");
            return;
        }
        System.out.printf("%-5s | %-20s | %-10s%n", "ID", "Nome", "Stato");
        displayLine("----------------------------------------");
        for (Machine m : lista) {
            System.out.printf("%-5d | %-20s | %-10s%n", m.id(), m.name(), m.active() ? "ATTIVO" : "DISATTIVO");
        }
    }

    public void displayEsercizi(List<Exercise> lista) {
        displaySectionTitle("Elenco Esercizi");
        if (lista.isEmpty()) {
            displayLine("Nessun esercizio presente.");
            return;
        }
        System.out.printf("%-5s | %-20s | %-10s | %-15s%n", "ID", "Nome", "Stato", "Tipo");
        displayLine("------------------------------------------------------------");
        for (Exercise e : lista) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macchinario (" + e.machineId() + ")";
            System.out.printf("%-5d | %-20s | %-10s | %-15s%n", e.id(), e.name(), e.active() ? "ATTIVO" : "DISATTIVO", tipo);
        }
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
