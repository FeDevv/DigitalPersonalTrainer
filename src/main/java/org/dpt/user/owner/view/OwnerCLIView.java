package org.dpt.user.owner.view;

import org.dpt.auth.Role;
import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.shared.mvc.AbstractCLIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente di visualizzazione CLI specializzato per il modulo Proprietario.
 * -
 * Estende {@link AbstractCLIView} fornendo metodi di rendering per i menu 
 * amministrativi e le tabelle del catalogo tecnico (Macchinari ed Esercizi).
 * Garantisce una rappresentazione chiara degli stati operativi degli asset.
 */
@SuppressWarnings("java:S106")
public class OwnerCLIView extends AbstractCLIView {

    private static final String GO_BACK = "Torna indietro";

    /**
     * Visualizza l'header personalizzato per la dashboard del proprietario.
     */
    public void displayOwnerHeader(String name) {
        displayHeader("PANNELLO PROPRIETARIO - Benvenuto/a " + name);
    }

    /** Menu principale con macro-categorie gestionali. */
    public void displayMainMenu() {
        displaySectionTitle("Amministrazione Centrale");
        displayLine("1. Gestione MACCHINARI");
        displayLine("2. Gestione ESERCIZI");
        displayLine("3. Gestione UTENZE (Staff/Clienti)");
        displayLine("0. Logout");
    }

    /** Menu operativo per la gestione dell'attrezzatura. */
    public void displayMachineMenu() {
        displaySectionTitle("Gestione MACCHINARI");
        displayLine("1. Visualizza tutti i MACCHINARI");
        displayLine("2. Attiva/Disattiva MACCHINARIO");
        displayLine("3. Registra nuovo MACCHINARIO");
        displayLine("0. " + GO_BACK);
    }

    /** Menu operativo per il catalogo degli esercizi. */
    public void displayExercisesMenu() {
        displaySectionTitle("Gestione ESERCIZI");
        displayLine("1. Visualizza tutti gli ESERCIZI");
        displayLine("2. Attiva/Disattiva ESERCIZIO");
        displayLine("3. Registra nuovo ESERCIZIO");
        displayLine("0. " + GO_BACK);
    }

    /** Menu per la selezione dell'ambito anagrafico. */
    public void displayUsersMenu() {
        displaySectionTitle("Gestione UTENZE");
        displayLine("1. Amministrazione PERSONAL TRAINER");
        displayLine("2. Amministrazione ADDETTI SEGRETERIA");
        displayLine("3. Amministrazione CLIENTI");
        displayLine("0. " + GO_BACK);
    }

    /** Menu dinamico per le azioni su una specifica tipologia di utente. */
    public void displayUserActionMenu(Role role) {
        displaySectionTitle("Azioni su " + role.getSingular());
        displayLine("1. Elenca " + role.getPlural());
        displayLine("2. Switch stato account (Attiva/Disattiva)");
        if (role != Role.CLIENT) {
            displayLine("3. Registra nuovo " + role.getSingular());
        }
        displayLine("0. " + GO_BACK);
    }

    /** Renderizza la tabella dei macchinari con evidenza dello stato. */
    public void displayMachines(List<Machine> lista) {
        displaySectionTitle("Elenco Tecnico Macchinari");
        
        String[] headers = {"ID", "DENOMINAZIONE MACCHINARIO", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (Machine m : lista) {
            rows.add(new String[]{
                String.valueOf(m.id()),
                m.name(),
                m.active() ? "ATTIVO" : "FUORI SERVIZIO"
            });
        }
        
        renderTable(headers, rows, new int[]{5, 35, 15});
    }

    /** Renderizza la tabella degli esercizi evidenziando l'associazione con le macchine. */
    public void displayExercises(List<Exercise> lista) {
        displaySectionTitle("Elenco Tecnico Esercizi");
        
        String[] headers = {"ID", "NOME ESERCIZIO", "STATO", "POSTAZIONE"};
        List<String[]> rows = new ArrayList<>();
        for (Exercise e : lista) {
            String postazione = e.bodyweight() ? "Corpo Libero" : "Macchina ID: " + e.machineId();
            rows.add(new String[]{
                String.valueOf(e.id()),
                e.name(),
                e.active() ? "ATTIVO" : "DISATTIVO",
                postazione
            });
        }
        
        renderTable(headers, rows, new int[]{5, 31, 10, 20});
    }

    /** Saluto di chiusura sessione. */
    public void displayGoodbye() {
        displayLine("\n Sessione Proprietario terminata correttamente. Arrivederci!");
    }
}
