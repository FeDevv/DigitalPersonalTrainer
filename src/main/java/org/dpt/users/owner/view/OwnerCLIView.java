package org.dpt.users.owner.view;

import org.dpt.shared.auth.Role;
import org.dpt.shared.catalog.esercizi.model.Exercise;
import org.dpt.shared.catalog.macchinari.model.Machine;
import org.dpt.shared.ui.BaseCLIView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S106")
public class OwnerCLIView extends BaseCLIView {

    public void displayOwnerHeader(String name) {
        displayHeader("PANNELLO PROPRIETARIO - Benvenuto/a " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Gestione MACCHINARI");
        displayLine("2. Gestione ESERCIZI");
        displayLine("3. Gestione UTENZE (Staff/Clienti)");
        displayLine("0. Logout");
    }

    public void displayMacchinariMenu() {
        displaySectionTitle("Gestione MACCHINARI");
        displayLine("1. Visualizza tutti i MACCHINARI");
        displayLine("2. Attiva/Disattiva MACCHINARIO");
        displayLine("3. Inserisci nuovo MACCHINARIO");
        displayLine("0. Torna indietro");
    }

    public void displayEserciziMenu() {
        displaySectionTitle("Gestione ESERCIZI");
        displayLine("1. Visualizza tutti gli ESERCIZI");
        displayLine("2. Attiva/Disattiva ESERCIZIO");
        displayLine("3. Inserisci nuovo ESERCIZIO");
        displayLine("0. Torna indietro");
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
        if (role != Role.CLIENT) {
            displayLine("3. Inserisci nuovo " + role.getSingular());
        }
        displayLine("0. Torna indietro");
    }

    public void displayMacchinari(List<Machine> lista) {
        displaySectionTitle("Elenco Macchinari");
        
        String[] headers = {"ID", "NOME MACCHINARIO", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (Machine m : lista) {
            rows.add(new String[]{
                String.valueOf(m.id()),
                m.name(),
                m.active() ? "ATTIVO" : "DISATTIVO"
            });
        }
        
        renderTable(headers, rows, new int[]{5, 30, 10});
    }

    public void displayEsercizi(List<Exercise> lista) {
        displaySectionTitle("Elenco Esercizi");
        
        String[] headers = {"ID", "NOME ESERCIZIO", "STATO", "TIPO/MACCHINA"};
        List<String[]> rows = new ArrayList<>();
        for (Exercise e : lista) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macch. ID: " + e.machineId();
            rows.add(new String[]{
                String.valueOf(e.id()),
                e.name(),
                e.active() ? "ATTIVO" : "DISATTIVO",
                tipo
            });
        }
        
        renderTable(headers, rows, new int[]{5, 31, 10, 20});
    }

    public void displayGoodbye() {
        displayLine("\n Logout effettuato. Arrivederci!");
    }
}
