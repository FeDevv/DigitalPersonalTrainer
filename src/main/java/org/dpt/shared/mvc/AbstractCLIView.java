package org.dpt.shared.mvc;

import org.dpt.domain.user.User;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Fornitore di primitive grafiche e toolkit di formattazione per interfacce CLI.
 * -
 * Questa classe agisce come astrazione base per tutte le View del sistema operanti 
 * in modalità terminale. Incapsula la complessità del Box-Drawing (UTF-8) e della 
 * formattazione tabellare dinamica, assicurando che la User Experience sia 
 * consistente, leggibile e visivamente curata in ogni modulo dell'applicazione.
 */
@SuppressWarnings("java:S106")
public class AbstractCLIView {

    // Caratteri Box-Drawing (Standard UTF-8) per la costruzione di tabelle e cornici
    protected static final String TL = "┌";
    protected static final String TR = "┐";
    protected static final String BL = "└";
    protected static final String BR = "┘";
    protected static final String H = "─";
    protected static final String V = "│";
    protected static final String TJ = "┬";
    protected static final String BJ = "┴";
    protected static final String LJ = "├";
    protected static final String RJ = "┤";
    protected static final String CJ = "┼";

    /**
     * Visualizza il logo ASCII dell'applicazione (Identità Visiva).
     */
    public void displayWelcomeBanner() {
        System.out.println("\n");
        System.out.println("  ██████╗ ██████╗ ████████╗");
        System.out.println("  ██╔══██╗██╔══██╗╚══██╔══╝");
        System.out.println("  ██║  ██║██████╔╝   ██║   ");
        System.out.println("  ██║  ██║██╔═══╝    ██║   ");
        System.out.println("  ██████╔╝██║        ██║   ");
        System.out.println("  ╚═════╝ ╚═╝        ╚═╝   ");
        System.out.println("   DIGITAL PERSONAL TRAINER");
        System.out.println();
    }

    /**
     * Renderizza l'intestazione principale di un modulo.
     * @param title Nome del modulo da visualizzare.
     */
    public void displayHeader(String title) {
        String content = " DPT - " + title.toUpperCase() + " ";
        int width = content.length() + 2;
        
        System.out.println("\n" + TL + H.repeat(width) + TR);
        System.out.println(V + " " + content + " " + V);
        System.out.println(BL + H.repeat(width) + BR);
    }

    /**
     * Visualizza un titolo di sezione sottolineato.
     */
    public void displaySectionTitle(String title) {
        System.out.println("\n " + title.toUpperCase());
        System.out.println(" " + H.repeat(title.length()));
    }

    /**
     * Renderizza una tabella bordata complessa con allineamento dinamico delle colonne.
     * Implementa un fallback visivo automatico nel caso in cui la lista dei dati sia vuota.
     * 
     * @param headers Array delle intestazioni delle colonne.
     * @param rows Lista di righe (array di stringhe).
     * @param colWidths Array delle larghezze fisse per ogni colonna.
     */
    public void renderTable(String[] headers, List<String[]> rows, int[] colWidths) {
        // Top border
        printTableSeparator(TL, TJ, TR, colWidths);

        // Header
        renderRow(headers, colWidths);

        // Header separator
        printTableSeparator(LJ, CJ, RJ, colWidths);

        // Rows content management
        if (rows.isEmpty()) {
            int totalWidth = 0;
            for (int w : colWidths) totalWidth += w + 2;
            totalWidth += colWidths.length - 1;
            String emptyFormat = "%s %-" + totalWidth + "s %s%n";
            System.out.printf(emptyFormat, V, "Nessun dato disponibile nel catalogo.", V);
        } else {
            for (String[] row : rows) {
                renderRow(row, colWidths);
            }
        }

        // Bottom border
        printTableSeparator(BL, BJ, BR, colWidths);
    }

    /**
     * Stampa una riga di dati formattata rispettando le larghezze delle colonne.
     */
    private void renderRow(String[] fields, int[] colWidths) {
        System.out.print(V);
        for (int i = 0; i < fields.length; i++) {
            String format = " %-" + colWidths[i] + "s %s";
            System.out.printf(format, fields[i], V);
        }
        System.out.println();
    }

    /** Genera i separatori orizzontali delle tabelle (bordi e giunzioni). */
    private void printTableSeparator(String left, String mid, String right, int[] widths) {
        System.out.print(left);
        for (int i = 0; i < widths.length; i++) {
            System.out.print(H.repeat(widths[i] + 2));
            if (i < widths.length - 1) System.out.print(mid);
        }
        System.out.println(right);
    }

    /** Visualizza il prompt per l'input utente. */
    public void displayInputPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            System.out.print("\n » ");
        } else {
            System.out.print(" " + prompt + " ");
        }
    }

    /** Notifica un errore critico o di validazione. */
    public void displayError(String message) {
        System.out.println("\n [!] ERRORE: " + message);
    }

    /** Notifica il successo di un'operazione. */
    public void displaySuccess(String message) {
        System.out.println("\n [*] OK: " + message);
    }

    public void displayLine(String text) {
        System.out.println(" " + text);
    }

    public void displayEmptyLine() {
        System.out.println();
    }

    /**
     * Metodo helper specializzato per la renderizzazione atomica di una lista utenti.
     * @param users Lista di entità che estendono User.
     * @param title Descrizione del ruolo visualizzato (es. "Personal Trainer").
     */
    public void renderUserTable(List<? extends User> users, String title) {
        displaySectionTitle("Elenco " + title);

        String[] headers = {"ID", "NOMINATIVO COMPLETO", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (User u : users) {
            rows.add(new String[]{
                    String.valueOf(u.getId()),
                    u.getFirstName() + " " + u.getLastName(),
                    u.isActive() ? "ATTIVO" : "DISATTIVO"
            });
        }

        renderTable(headers, rows, new int[]{5, 30, 10});
    }

    /**
     * Renderizza il dettaglio tecnico degli esercizi contenuti in una scheda d'allenamento.
     * Metodo condiviso tra l'area PT e l'area Cliente per garantire coerenza visuale.
     *
     * @param title Titolo della sezione (es. nome della scheda).
     * @param details Lista di item della scheda (esercizi, serie, reps, recupero).
     */
    public void renderExerciseTable(String title, List<ActiveSheetItem> details) {
        displaySectionTitle(title);

        String[] headers = {"ESERCIZIO", "SERIE", "REP", "RECUPERO", "NOTE"};
        List<String[]> rows = new ArrayList<>();
        for (ActiveSheetItem item : details) {
            rows.add(new String[]{
                    item.exerciseName(),
                    String.valueOf(item.expectedSets()),
                    String.valueOf(item.expectedReps()),
                    item.restTime() + "s",
                    item.executionNotes() != null ? item.executionNotes() : "-"
            });
        }

        renderTable(headers, rows, new int[]{31, 6, 6, 10, 25});
    }
}
