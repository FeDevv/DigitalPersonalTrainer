package org.DPT.dirty.proprietario.view;

import org.DPT.dirty.shared.catalogo.model.Esercizio;
import org.DPT.dirty.shared.catalogo.model.Macchinario;
import org.DPT.dirty.shared.model.UtenteSommario;

import java.util.List;

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

    public void displayLabel(String label) {
        System.out.print(label + ": ");
    }

    public void displayError(String message) {
        System.out.println("[ERRORE] " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("\n[OK] " + message);
    }

    public void displayGoodbye() {
        System.out.println("\nChiusura sessione amministrativa. Arrivederci!");
    }

    public void displayListHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    // --- VISUALIZZAZIONE DATI TABELLARI ---

    public void displayMacchinari(List<Macchinario> lista) {
        displayListHeader("LISTA MACCHINARI");
        for (var m : lista) {
            String stato = m.attivo() ? "[OK]" : "[DISATTIVATO]";
            System.out.printf("%d) %-25s %s %n", m.id(), m.nome(), stato);
        }
    }

    public void displayEsercizi(List<Esercizio> lista) {
        displayListHeader("LISTA ESERCIZI");
        for (var e : lista) {
            String stato = e.attivo() ? "[OK]" : "[DISATTIVATO]";
            String tipo = e.corpoLibero() ? "(Corpo Libero)" : "(ID Macchina: " + e.idMacchinario() + ")";
            System.out.printf("%d) %-30s %-20s %s %n", e.codice(), e.nome(), tipo, stato);
        }
    }

    public void displayUtenti(List<UtenteSommario> lista, String titolo) {
        displayListHeader("LISTA " + titolo);
        System.out.printf("%-5s %-25s %-30s %s %n", "ID", "NOMINATIVO", "EMAIL", "STATO");
        System.out.println("--------------------------------------------------------------------------------");
        for (var u : lista) {
            String stato = u.attivo() ? "[ATTIVO]" : "[DISATTIVATO]";
            System.out.printf("%-5d %-25s %-30s %s %n", u.id(), u.getNomeCompleto(), u.email(), stato);
        }
    }
}
