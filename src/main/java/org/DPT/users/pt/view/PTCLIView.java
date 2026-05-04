package org.DPT.users.pt.view;

import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.ui.BaseCLIView;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.model.Client;
import org.DPT.users.pt.controller.PTUI;

import java.util.List;

public class PTCLIView extends BaseCLIView {

    public void displayPTHeader(String name) {
        displayHeader("Pannello Personal Trainer - PT: " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Crea Nuova Scheda Cliente");
        displayLine("2. Visualizza Storico Schede Redatte");
        displayLine("3. Genera Report Prestazioni Clienti");
        displayLine("4. Consulta Catalogo (Macchinari ed Esercizi)");
        displayLine("0. Logout");
    }

    public void displayClients(List<Client> clients) {
        displaySectionTitle("Clienti Assegnati");
        if (clients.isEmpty()) {
            displayLine("Nessun cliente assegnato attualmente.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-20s%n", "ID", "Nominativo", "Codice Fiscale");
        displayLine("------------------------------------------------------------");
        for (Client c : clients) {
            System.out.printf("%-5d | %-25s | %-20s%n", c.getId(), c.getFullName(), c.getFiscalCode());
        }
    }

    public void displayExerciseCatalog(List<Exercise> exercises) {
        displaySectionTitle("Catalogo Esercizi");
        if (exercises.isEmpty()) {
            displayLine("Catalogo vuoto.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-20s%n", "ID", "Nome Esercizio", "Tipo");
        displayLine("------------------------------------------------------------");
        for (Exercise e : exercises) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macchinario (ID: " + e.machineId() + ")";
            System.out.printf("%-5d | %-25s | %-20s%n", e.id(), e.name(), tipo);
        }
    }

    public void displaySheetHistory(List<WorkoutSheet> sheets) {
        displaySectionTitle("Storico Schede Redatte");
        if (sheets.isEmpty()) {
            displayLine("Nessuna scheda trovata nel tuo storico.");
            return;
        }
        System.out.printf("%-5s | %-10s | %-20s | %-10s | %-10s%n", "ID", "Cliente", "Titolo", "Data", "Stato");
        displayLine("---------------------------------------------------------------------------");
        for (WorkoutSheet s : sheets) {
            System.out.printf("%-5d | %-10d | %-20s | %-10s | %-10s%n", 
                    s.id(), s.clientId(), s.title(), s.creationDate(), s.active() ? "ATTIVA" : "ARCHIVIATA");
        }
    }

    public void displayPerformanceReport(List<PTUI.PerformanceRecord> report) {
        displaySectionTitle("Report Prestazioni Clienti");
        if (report.isEmpty()) {
            displayLine("Nessun dato trovato per l'intervallo specificato.");
            return;
        }
        System.out.printf("%-20s | %-12s | %-10s | %-10s%n", "Cliente", "Data", "Durata(m)", "Completamento");
        displayLine("---------------------------------------------------------------------------");
        for (PTUI.PerformanceRecord r : report) {
            System.out.printf("%-20s | %-12s | %-10d | %-10s%n", 
                    r.clientName(), r.date(), r.duration(), r.completionPercentage() + "%");
        }
    }

    public void displayCatalog(List<Machine> machines, List<Exercise> exercises) {
        displaySectionTitle("Consultazione Catalogo");
        
        displaySectionTitle("Macchinari Disponibili");
        if (machines.isEmpty()) displayLine("Nessun macchinario.");
        else {
            for (Machine m : machines) {
                System.out.printf("[%d] %s - %s%n", m.id(), m.name(), m.description());
            }
        }

        displaySectionTitle("Esercizi Disponibili");
        if (exercises.isEmpty()) displayLine("Nessun esercizio.");
        else {
            for (Exercise e : exercises) {
                String tipo = e.bodyweight() ? "Corpo Libero" : "Macchina ID: " + e.machineId();
                System.out.printf("[%d] %s (%s)%n", e.id(), e.name(), tipo);
            }
        }
    }

    public void displayGoodbye() {
        displayLine("\nLogout effettuato. Buon lavoro, Coach!");
    }
}
