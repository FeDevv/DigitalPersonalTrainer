package org.dpt.shared.mvc;

import java.util.Scanner;

/**
 * Controller specializzato per la gestione dell'interazione utente tramite riga di comando.
 * -
 * Implementa la logica di acquisizione dati robusta, gestendo scenari comuni 
 * di errore (input non numerici, stringhe vuote) e garantendo la corretta 
 * gestione del buffer dello Scanner (newline consumption).
 * -
 * Seguendo il principio DRY, centralizza le operazioni di I/O ripetitive per
 * garantire uniformità nel feedback verso l'utente.
 */
public abstract class AbstractCLIController {

    /** Canale di input condiviso, gestito centralmente per evitare conflitti di lettura. */
    protected final Scanner scanner;
    
    /** Riferimento alla View CLI per la renderizzazione dei prompt e dei messaggi. */
    protected final AbstractCLIView view;

    protected AbstractCLIController(Scanner scanner, AbstractCLIView view) {
        this.scanner = scanner;
        this.view = view;
    }

    /**
     * Acquisisce un intero in modo sicuro.
     * Implementa un loop di riprova automatico in caso di errore di formato (InputMismatch).
     * @param prompt Testo da visualizzare per la richiesta.
     * @return L'intero validato.
     */
    protected int readInt(String prompt) {
        view.displayInputPrompt(prompt);
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido (es. 1, 2, 3).");
            scanner.nextLine(); // Svuotamento preventivo del buffer in caso di errore
            view.displayInputPrompt(prompt);
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // Consumo esplicito del carattere 'newline' residuo
        return val;
    }

    /**
     * Acquisisce una stringa garantendone la non-vacuità.
     * Implementa un loop di validazione locale per impedire input di soli spazi o nulli.
     * @param label Etichetta descrittiva del campo richiesto.
     * @return La stringa acquisita e pulita (trimmed).
     */
    protected String readString(String label) {
        view.displayInputPrompt(label);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            view.displayError("Il campo è obbligatorio e non può essere lasciato vuoto.");
            view.displayInputPrompt(label);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    /**
     * Acquisisce una stringa opzionale.
     * @param label Etichetta descrittiva.
     * @return La stringa (anche vuota) processata con trim().
     */
    protected String readOptionalString(String label) {
        view.displayInputPrompt(label);
        return scanner.nextLine().trim();
    }

    /** Inoltra la segnalazione di un errore alla view CLI. */
    public void reportError(String message) {
        view.displayError(message);
    }
}
