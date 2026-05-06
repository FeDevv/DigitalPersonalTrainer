package org.DPT.users.pt.view;

import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.ui.BaseCLIView;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.model.Client;
import org.DPT.users.pt.controller.PTUI;

import java.util.ArrayList;
import java.util.List;

public class PTCLIView extends BaseCLIView {

    public void displayPTHeader(String name) {
        displayHeader("PANNELLO PERSONAL TRAINER - Benvenuto/a " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Crea Nuova SCHEDA CLIENTE");
        displayLine("2. Visualizza STORICO SCHEDE Redatte");
        displayLine("3. Genera REPORT PRESTAZIONI Clienti");
        displayLine("4. Consulta CATALOGO (Macchinari ed Esercizi)");
        displayLine("0. Logout");
    }

    public void displayClients(List<Client> clients) {
        displaySectionTitle("Clienti Assegnati");
        
        String[] headers = {"ID", "NOMINATIVO", "CODICE FISCALE"};
        List<String[]> rows = new ArrayList<>();
        for (Client c : clients) {
            rows.add(new String[]{
                String.valueOf(c.getId()),
                c.getFullName(),
                c.getFiscalCode()
            });
        }
        
        renderTable(headers, rows, new int[]{5, 25, 20});
    }

    public void displayExerciseCatalog(List<Exercise> exercises) {
        displaySectionTitle("Catalogo Esercizi");
        
        String[] headers = {"ID", "NOME ESERCIZIO", "TIPO/MACCHINA"};
        List<String[]> rows = new ArrayList<>();
        for (Exercise e : exercises) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macchina ID: " + e.machineId();
            rows.add(new String[]{
                String.valueOf(e.id()),
                e.name(),
                tipo
            });
        }
        
        renderTable(headers, rows, new int[]{5, 25, 20});
    }

    public void displaySheetHistory(List<WorkoutSheet> sheets) {
        displaySectionTitle("Storico Schede Redatte");
        
        String[] headers = {"ID", "CLI ID", "TITOLO SCHEDA", "DATA", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (WorkoutSheet s : sheets) {
            rows.add(new String[]{
                String.valueOf(s.id()),
                String.valueOf(s.clientId()),
                s.title(),
                s.creationDate().toString(),
                s.active() ? "ATTIVA" : "ARCHIVIATA"
            });
        }
        
        renderTable(headers, rows, new int[]{5, 6, 25, 12, 12});
    }

    public void displayPerformanceReport(List<PTUI.PerformanceRecord> report) {
        displaySectionTitle("Report Prestazioni Clienti");
        
        String[] headers = {"CLIENTE", "DATA", "DURATA(m)", "COMPLETAMENTO"};
        List<String[]> rows = new ArrayList<>();
        for (PTUI.PerformanceRecord r : report) {
            rows.add(new String[]{
                r.clientName(),
                r.date().toString(),
                String.valueOf(r.duration()),
                r.completionPercentage() + "%"
            });
        }
        
        renderTable(headers, rows, new int[]{20, 12, 10, 15});
    }

    public void displayCatalog(List<Machine> machines, List<Exercise> exercises) {
        displaySectionTitle("Consultazione Catalogo");
        
        displaySectionTitle("Macchinari Disponibili");
        String[] mHeaders = {"ID", "NOME MACCHINARIO", "DESCRIZIONE"};
        List<String[]> mRows = new ArrayList<>();
        for (Machine m : machines) {
            mRows.add(new String[]{
                String.valueOf(m.id()),
                m.name(),
                m.description() != null ? m.description() : "-"
            });
        }
        renderTable(mHeaders, mRows, new int[]{5, 25, 40});

        displaySectionTitle("Esercizi Disponibili");
        String[] eHeaders = {"ID", "NOME ESERCIZIO", "TIPO/MACCHINA"};
        List<String[]> eRows = new ArrayList<>();
        for (Exercise e : exercises) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macchina ID: " + e.machineId();
            eRows.add(new String[]{
                String.valueOf(e.id()),
                e.name(),
                tipo
            });
        }
        renderTable(eHeaders, eRows, new int[]{5, 25, 25});
    }

    public void displayGoodbye() {
        displayLine("\n Logout effettuato. Buon lavoro, Coach!");
    }
}
