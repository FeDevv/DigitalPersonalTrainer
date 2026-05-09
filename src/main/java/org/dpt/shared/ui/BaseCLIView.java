package org.dpt.shared.ui;

import org.dpt.users.common.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe base per tutte le View CLI del sistema.
 * Fornisce metodi standard per la formattazione dell'output tramite tabelle e box.
 */
@SuppressWarnings("java:S106")
public class BaseCLIView {

    // Caratteri Box-Drawing per tabelle e cornici
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

    public void displayHeader(String title) {
        String content = " DPT - " + title.toUpperCase() + " ";
        int width = content.length() + 2;
        
        System.out.println("\n" + TL + H.repeat(width) + TR);
        System.out.println(V + " " + content + " " + V);
        System.out.println(BL + H.repeat(width) + BR);
    }

    public void displaySectionTitle(String title) {
        System.out.println("\n " + title.toUpperCase());
        System.out.println(" " + H.repeat(title.length()));
    }

    /**
     * Renderizza una tabella bordata in modo automatico.
     */
    public void renderTable(String[] headers, List<String[]> rows, int[] colWidths) {
        // Top border
        printTableSeparator(TL, TJ, TR, colWidths);

        // Header
        System.out.print(V);
        for (int i = 0; i < headers.length; i++) {
            System.out.printf(" %-" + colWidths[i] + "s " + V, headers[i]);
        }
        System.out.println();

        // Header separator
        printTableSeparator(LJ, CJ, RJ, colWidths);

        // Rows
        if (rows.isEmpty()) {
            int totalWidth = 0;
            for (int w : colWidths) totalWidth += w + 2;
            totalWidth += colWidths.length - 1;
            System.out.printf(V + " %-" + totalWidth + "s " + V + "%n", "Nessun dato disponibile.");
        } else {
            for (String[] row : rows) {
                System.out.print(V);
                for (int i = 0; i < row.length; i++) {
                    System.out.printf(" %-" + colWidths[i] + "s " + V, row[i]);
                }
                System.out.println();
            }
        }

        // Bottom border
        printTableSeparator(BL, BJ, BR, colWidths);
    }

    private void printTableSeparator(String left, String mid, String right, int[] widths) {
        System.out.print(left);
        for (int i = 0; i < widths.length; i++) {
            System.out.print(H.repeat(widths[i] + 2));
            if (i < widths.length - 1) System.out.print(mid);
        }
        System.out.println(right);
    }

    // non usato ma utile da tenere per future espansioni
    public void displayLabel(String label) {
        System.out.print(label);
    }

    public void displayInputPrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            System.out.print("\n » ");
        } else {
            System.out.print(" " + prompt + " ");
        }
    }

    public void displayError(String message) {
        System.out.println("\n [!] ERRORE: " + message);
    }

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
     * Renderizza una tabella standard per la visualizzazione di una lista di utenti.
     * Centralizzato per rispettare il principio DRY tra le diverse View.
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
}
