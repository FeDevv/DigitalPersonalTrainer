package org.DPT.dirty.proprietario.controller;

import org.DPT.dirty.shared.catalogo.model.Esercizio;
import org.DPT.dirty.shared.catalogo.model.Macchinario;
import org.DPT.dirty.proprietario.view.ProprietarioCLIView;
import org.DPT.dirty.shared.model.UtenteSommario;

import java.util.Scanner;
import java.util.List;

/**
 * Interface Controller per la versione CLI del modulo Proprietario.
 * Gestisce l'acquisizione dell'input con validazione di base e delega la visualizzazione alla View.
 */
public class ProprietarioCLIController {

    private final ProprietarioCLIView view;
    private final Scanner scanner;

    public ProprietarioCLIController(Scanner scanner) {
        this.view = new ProprietarioCLIView();
        this.scanner = scanner;
    }

    public void showHeader() { view.displayHeader(); }
    public void showMainMenu() { view.displayMainMenu(); }
    public void showMacchinariMenu() { view.displayMacchinariSubMenu(); }
    public void showEserciziMenu() { view.displayEserciziSubMenu(); }
    public void showUtenzeMenu() { view.displayUtenzeMenu(); }
    public void showUtenzaActionMenu(String tipo) { view.displayUtenzaActionMenu(tipo); }
    public void reportError(String message) { view.displayError(message); }
    public void reportGoodbye() { view.displayGoodbye(); }
    public void reportSuccess(String message) { view.displaySuccess(message); }

    public int askForChoice() {
        return readInt("\n>> ");
    }

    // --- HELPERS GENERICI PER INPUT ---

    /**
     * Legge una stringa non vuota.
     */
    private String readString(String label) {
        view.displayLabel(label);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            view.displayError("Il campo non può essere vuoto.");
            view.displayLabel(label);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    /**
     * Legge un intero con gestione dell'errore di formato.
     */
    private int readInt(String label) {
        view.displayLabel(label);
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un numero valido.");
            scanner.nextLine(); // Pulisce il buffer
            view.displayLabel(label);
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline rimasto
        return val;
    }

    // --- METODI SEMANTICI (Wrapper per gli Helpers) ---

    // Catalogo
    public String askForNome() { return readString("Nome"); }
    public String askForDescrizione() { return readString("Descrizione"); }
    public int askForIDMacchinario() { return readInt("ID Macchinario (0 se corpo libero)"); }
    public int askForIDMacchinarioDaDisattivare() { return readInt("ID Macchinario da disattivare"); }
    public int askForIDEsercizio() { return readInt("ID Esercizio da attivare/disattivare"); }

    // Utenze
    public String askForNomePersona() { return readString("Nome"); }
    public String askForCognome() { return readString("Cognome"); }
    public String askForEmail() { return readString("Email"); }
    public String askForPassword() { return readString("Password"); }
    public String askForCF() { return readString("Codice Fiscale"); }
    public String askForIndirizzo() { return readString("Indirizzo Residenza"); }
    public String askForDataNascita() { return readString("Data Nascita (AAAA-MM-GG)"); }
    public int askForIDUtente() { return readInt("ID Utente da attivare/disattivare"); }

    // --- VISUALIZZAZIONE DATI ---

    public void showMacchinari(List<Macchinario> lista) {
        view.displayMacchinari(lista);
    }

    public void showEsercizi(List<Esercizio> lista) {
        view.displayEsercizi(lista);
    }

    public void showUtenti(List<UtenteSommario> lista, String titolo) {
        view.displayUtenti(lista, titolo);
    }
}
