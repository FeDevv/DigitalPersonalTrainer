package org.DPT.proprietario.controller;

import org.DPT.proprietario.view.ProprietarioCLIView;
import org.DPT.shared.model.UtenteSommario;

import java.util.Scanner;
import java.util.List;

/**
 * Interface Controller per la versione CLI del modulo Proprietario.
 */
public class ProprietarioCLIController {

    private final ProprietarioCLIView view;
    private final Scanner scanner;

    public ProprietarioCLIController(Scanner scanner) {
        this.view = new ProprietarioCLIView();
        this.scanner = scanner;
    }

    public void showHeader() {
        view.displayHeader();
    }

    public void showMainMenu() {
        view.displayMainMenu();
    }

    public void showMacchinariMenu() {
        view.displayMacchinariSubMenu();
    }

    public void showEserciziMenu() {
        view.displayEserciziSubMenu();
    }

    public void showUtenzeMenu() {
        view.displayUtenzeMenu();
    }

    public void showUtenzaActionMenu(String tipo) {
        view.displayUtenzaActionMenu(tipo);
    }

    public int askForChoice() {
        view.displayInputPrompt();
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un numero valido.");
            scanner.nextLine();
            view.displayInputPrompt();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline
        return choice;
    }

    public void reportError(String message) {
        view.displayError(message);
    }

    public void reportGoodbye() {
        view.displayGoodbye();
    }

    public void reportSuccess(String message) {
        view.displaySuccess(message);
    }

    // --- ACQUISIZIONE DATI CATALOGO ---

    public String askForNome() {
        view.promptNomeMacchinario();
        return scanner.nextLine().trim();
    }

    public String askForDescrizione() {
        view.promptDescrizione();
        return scanner.nextLine().trim();
    }

    public int askForIDMacchinario() {
        view.promptIDMacchinario();
        return readInt();
    }

    public int askForIDMacchinarioDaDisattivare() {
        view.promptIDMacchinarioDaDisattivare();
        return readInt();
    }

    public int askForIDEsercizio() {
        view.promptIDEsercizio();
        return readInt();
    }

    // --- ACQUISIZIONE DATI UTENZE ---

    public String askForNomePersona() { view.promptNome(); return scanner.nextLine().trim(); }
    public String askForCognome() { view.promptCognome(); return scanner.nextLine().trim(); }
    public String askForEmail() { view.promptEmail(); return scanner.nextLine().trim(); }
    public String askForPassword() { view.promptPassword(); return scanner.nextLine().trim(); }
    public String askForCF() { view.promptCF(); return scanner.nextLine().trim(); }
    public String askForIndirizzo() { view.promptIndirizzo(); return scanner.nextLine().trim(); }
    public String askForDataNascita() { view.promptDataNascita(); return scanner.nextLine().trim(); }
    public int askForIDUtente() { view.promptIDUtente(); return readInt(); }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un numero valido.");
            scanner.nextLine();
            view.displayInputPrompt();
        }
        int val = scanner.nextInt();
        scanner.nextLine(); 
        return val;
    }

    // --- VISUALIZZAZIONE DATI ---

    public void showMacchinari(List<org.DPT.shared.catalogo.model.Macchinario> lista) {
        view.displayListHeader("LISTA MACCHINARI");
        for (var m : lista) {
            String stato = m.attivo() ? "[OK]" : "[DISATTIVATO]";
            System.out.printf("%d) %-25s %s %n", m.id(), m.nome(), stato);
        }
    }

    public void showEsercizi(List<org.DPT.shared.catalogo.model.Esercizio> lista) {
        view.displayListHeader("LISTA ESERCIZI");
        for (var e : lista) {
            String stato = e.attivo() ? "[OK]" : "[DISATTIVATO]";
            String tipo = e.corpoLibero() ? "(Corpo Libero)" : "(ID Macchina: " + e.idMacchinario() + ")";
            System.out.printf("%d) %-30s %-20s %s %n", e.codice(), e.nome(), tipo, stato);
        }
    }

    public void showUtenti(List<UtenteSommario> lista, String titolo) {
        view.displayListHeader("LISTA " + titolo);
        System.out.printf("%-5s %-25s %-30s %s %n", "ID", "NOMINATIVO", "EMAIL", "STATO");
        System.out.println("--------------------------------------------------------------------------------");
        for (var u : lista) {
            String stato = u.attivo() ? "[ATTIVO]" : "[DISATTIVATO]";
            System.out.printf("%-5d %-25s %-30s %s %n", u.id(), u.getNomeCompleto(), u.email(), stato);
        }
    }
}
