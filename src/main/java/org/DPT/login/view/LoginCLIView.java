package org.DPT.login.view;

import org.DPT.auth.Role;

/**
 * Gestisce ESCLUSIVAMENTE l'output testuale per il modulo di Login.
 */
public class LoginCLIView {

    public void displayHeader() {
        System.out.println("\n========================================");
        System.out.println("     DIGITAL PERSONAL TRAINER - LOGIN   ");
        System.out.println("========================================");
    }

    public void displayRoleMenu(Role[] roles) {
        System.out.println("\nSeleziona il tipo di utenza:");

        for (Role role : roles) {
            if (role != Role.LOGIN) {
                System.out.println(role.getId() + ") " + role.getDescription());
            }
        }
        System.out.println("0) Esci");
        // Rimosso il prompt da qui: ora lo gestisce il controller in askForChoice
    }

    public void displayInputPrompt() {
        System.out.print("\n>> ");
    }

    public void promptEmail() {
        System.out.print("Email: ");
    }

    public void promptPassword() {
        System.out.print("Password: ");
    }

    public void displayError(String message) {
        System.out.println("[ERRORE] " + message);
    }

    public void displaySuccess(String nomeCompleto, Role role) {
        System.out.println("\n----------------------------------------");
        System.out.println("Accesso eseguito come: " + nomeCompleto);
        System.out.println("Ruolo: " + role.getDescription());
        System.out.println("----------------------------------------\n");
    }

    public void displayGoodbye() {
        System.out.println("\nUscita dal modulo di Login. A presto!");
    }
}
