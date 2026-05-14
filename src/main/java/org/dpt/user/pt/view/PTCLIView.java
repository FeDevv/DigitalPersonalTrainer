package org.dpt.user.pt.view;

import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.shared.mvc.AbstractCLIView;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.model.Client;
import org.dpt.user.pt.model.PerformanceDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente di visualizzazione CLI specializzato per le funzionalità del Personal Trainer.
 * -
 * Estende {@link AbstractCLIView} fornendo rendering tabellari avanzati per lo storico
 * delle schede, l'anagrafica atleti assegnati e i report analitici sulle prestazioni,
 * garantendo una chiara distinzione tra i parametri previsti e quelli effettivi.
 */
@SuppressWarnings("java:S106")
public class PTCLIView extends AbstractCLIView {

    /** Visualizza l'header operativo per il Personal Trainer. */
    public void displayPTHeader(String name) {
        displayHeader("PANNELLO PT - Benvenuto/a " + name);
    }

    /** Menu principale con le aree di competenza tecnica. */
    public void displayMainMenu() {
        displaySectionTitle("Pannello Istruttore");
        displayLine("1. Redigi NUOVA SCHEDA per un Cliente");
        displayLine("2. Consulta STORICO SCHEDE Programmate");
        displayLine("3. Analizza PRESTAZIONI e Progressi Clienti");
        displayLine("4. Sfoglia CATALOGO Tecnico (Macchinari/Esercizi)");
        displayLine("0. Logout");
    }

    /** Renderizza la lista dei clienti associati professionalmente al PT. */
    public void displayClients(List<Client> clients) {
        displaySectionTitle("Anagrafica Atleti Assegnati");
        
        String[] headers = {"ID", "NOMINATIVO COMPLETO", "CODICE FISCALE"};
        List<String[]> rows = new ArrayList<>();
        for (Client c : clients) {
            rows.add(new String[]{
                String.valueOf(c.getId()),
                c.getFullName(),
                c.getFiscalCode()
            });
        }
        
        renderTable(headers, rows, new int[]{5, 30, 20});
    }

    /** Renderizza gli esercizi disponibili con indicazione del supporto meccanico. */
    public void displayExerciseCatalog(List<Exercise> exercises) {
        displaySectionTitle("Catalogo Tecnico Esercizi");
        
        String[] headers = {"ID", "NOME ESERCIZIO", "TIPOLOGIA / ATTREZZO"};
        List<String[]> rows = new ArrayList<>();
        for (Exercise e : exercises) {
            String tipo = e.bodyweight() ? "Corpo Libero" : "Macchina ID: " + e.machineId();
            rows.add(new String[]{
                String.valueOf(e.id()),
                e.name(),
                tipo
            });
        }
        
        renderTable(headers, rows, new int[]{5, 31, 20});
    }

    /** Visualizza la cronologia dei piani di allenamento redatti. */
    public void displaySheetHistory(List<WorkoutSheet> sheets) {
        displaySectionTitle("Registro Storico Programmazioni");
        
        String[] headers = {"ID", "ID CLIENTE", "TITOLO PIANO", "EMISSIONE", "STATO"};
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
        
        renderTable(headers, rows, new int[]{5, 13, 30, 12, 12});
    }

    /** Renderizza le statistiche aggregate sull'aderenza agli allenamenti. */
    public void displayPerformanceReport(List<PerformanceDTO> report) {
        displaySectionTitle("Analisi Prestazioni Atleti");
        
        String[] headers = {"NOMINATIVO CLIENTE", "TOT. ALLEN.", "DATA SESSIONE", "DURATA", "COMPLETAMENTO"};
        List<String[]> rows = new ArrayList<>();
        for (PerformanceDTO r : report) {
            rows.add(new String[]{
                r.clientName(),
                String.valueOf(r.totalWorkouts()),
                r.date().toString(),
                r.duration() + "m",
                r.completionPercentage() + "%"
            });
        }
        
        renderTable(headers, rows, new int[]{25, 12, 15, 10, 15});
    }

    /** Vista unificata del catalogo per il supporto alla programmazione. */
    public void displayCatalog(List<Machine> machines, List<Exercise> exercises) {
        displaySectionTitle("Consultazione Risorse Tecniche");
        
        displaySectionTitle("Parco Macchine Operativo");
        String[] mHeaders = {"ID", "NOME ATTREZZATURA", "NOTE TECNICHE"};
        List<String[]> mRows = new ArrayList<>();
        for (Machine m : machines) {
            mRows.add(new String[]{
                String.valueOf(m.id()),
                m.name(),
                m.description() != null ? m.description() : "N/D"
            });
        }
        renderTable(mHeaders, mRows, new int[]{5, 31, 40});

        displayExerciseCatalog(exercises);
    }

    /** Renderizza il dettaglio tecnico degli esercizi inseriti in una scheda. */
    public void displaySheetDetails(String title, List<ActiveSheetItem> details) {
        renderExerciseTable("Dettaglio Tecnico Scheda: " + title, details);
    }

    /** Saluto di chiusura sessione PT. */
    public void displayGoodbye() {
        displayLine("\n Sessione PT terminata. Buon lavoro con i prossimi atleti!");
    }
}
