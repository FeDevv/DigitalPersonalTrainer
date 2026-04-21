package org.DPT.login.view;

import org.DPT.auth.Role;

/**
 * Gestisce l'output testuale per il modulo di Login.
 * Fornisce metodi per stampare menu, prompt e messaggi di errore.
 */
public class LoginView {

    public void displayHeader() {
        System.out.println("\n========================================");
        System.out.println("     DIGITAL PERSONAL TRAINER - LOGIN   ");
        System.out.println("========================================\n");
    }

    public void displayRoleMenu() {
        System.out.println("Seleziona il tipo di utenza:");
        System.out.println("1. Proprietario");
        System.out.println("2. Personal Trainer");
        System.out.println("3. Segreteria");
        System.out.println("4. Cliente");
        System.out.println("0. Esci");
        System.out.print("\nScelta: ");
    }

    public void promptEmail() {
        System.out.print("Email: ");
    }

    public void promptPassword() {
        System.out.print("Password: ");
    }

    public void displayError(String message) {
        System.out.println("\n[ERRORE] " + message);
    }

    public void displaySuccess(String nomeCompleto, Role role) {
        System.out.println("\n----------------------------------------");
        System.out.println("Accesso eseguito come: " + nomeCompleto);
        System.out.println("Ruolo: " + role);
        System.out.println("----------------------------------------\n");
    }

    public void displayGoodbye() {
        System.out.println("\nGrazie per aver usato Digital Personal Trainer. A presto!");
    }
}
