package org.DPT.proprietario.view;

/**
 * Gestisce ESCLUSIVAMENTE l'output testuale per il modulo Proprietario.
 */
public class ProprietarioCLIView {

    public void displayHeader() {
        System.out.println("\n========================================");
        System.out.println("     DIGITAL PERSONAL TRAINER - ADMIN   ");
        System.out.println("========================================");
    }

    public void displayMainMenu() {
        System.out.println("\n--- MENU PRINCIPALE ---");
        System.out.println("1) Gestisci Macchinari");
        System.out.println("2) Gestisci Esercizi");
        System.out.println("3) Gestisci Utenze");
        System.out.println("0) Logout");
    }

    public void displayMacchinariSubMenu() {
        System.out.println("\n--- OPZIONI MACCHINARI ---");
        System.out.println("1) Visualizza Catalogo");
        System.out.println("2) Aggiungi Nuovo Macchinario");
        System.out.println("3) Cambia Stato (Attiva/Disattiva)");
        System.out.println("0) Torna indietro");
    }

    public void displayEserciziSubMenu() {
        System.out.println("\n--- OPZIONI ESERCIZI ---");
        System.out.println("1) Visualizza Catalogo");
        System.out.println("2) Aggiungi Nuovo Esercizio");
        System.out.println("3) Cambia Stato (Attiva/Disattiva)");
        System.out.println("0) Torna indietro");
    }

    public void displayUtenzeMenu() {
        System.out.println("\n--- GESTIONE UTENZE ---");
        System.out.println("1) Addetto Segreteria");
        System.out.println("2) Personal Trainer");
        System.out.println("3) Cliente");
        System.out.println("0) Torna al menu principale");
    }

    public void displayUtenzaActionMenu(String tipo) {
        System.out.println("\n--- GESTIONE " + tipo + " ---");
        System.out.println("1) Visualizza Lista");
        System.out.println("2) Inserisci Nuovo");
        System.out.println("3) Cambia Stato (Attiva/Disattiva)");
        System.out.println("0) Indietro");
    }

    public void displayInputPrompt() {
        System.out.print("\n>> ");
    }

    public void displayError(String message) {
        System.out.println("[ERRORE] " + message);
    }

    public void displayGoodbye() {
        System.out.println("\nChiusura sessione amministrativa. Arrivederci!");
    }

    // --- PROMPT PER CATALOGO ---

    public void promptNomeMacchinario() { System.out.print("Nome Macchinario: "); }
    public void promptDescrizione() { System.out.print("Descrizione: "); }
    public void promptIDMacchinario() { System.out.print("ID Macchinario (0 se corpo libero): "); }
    public void promptIDEsercizio() { System.out.print("ID Esercizio da disattivare: "); }
    public void promptIDMacchinarioDaDisattivare() { System.out.print("ID Macchinario da disattivare: "); }

    // --- PROMPT PER UTENZE ---

    public void promptNome() { System.out.print("Nome: "); }
    public void promptCognome() { System.out.print("Cognome: "); }
    public void promptEmail() { System.out.print("Email: "); }
    public void promptPassword() { System.out.print("Password: "); }
    public void promptCF() { System.out.print("Codice Fiscale: "); }
    public void promptIndirizzo() { System.out.print("Indirizzo Residenza: "); }
    public void promptDataNascita() { System.out.print("Data Nascita (AAAA-MM-GG): "); }
    public void promptIDUtente() { System.out.print("ID Utente da attivare/disattivare: "); }

    public void displaySuccess(String message) {
        System.out.println("\n[OK] " + message);
    }

    public void displayListHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}
