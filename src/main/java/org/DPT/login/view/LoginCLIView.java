package org.DPT.login.view;

import org.DPT.auth.Role;

/**
 * Gestisce ESCLUSIVAMENTE l'output testuale per il modulo di Login.
 * Non contiene logica decisionale e non legge input (niente Scanner).
 */
public class LoginCLIView {

    public void displayHeader() {
        System.out.println("\n========================================");
        System.out.println("     DIGITAL PERSONAL TRAINER - LOGIN   ");
        System.out.println("========================================\n");
    }

    /**
     * Stampa dinamicamente le opzioni basandosi sull'Enum Role.
     * @param roles L'array di tutti i ruoli possibili nel sistema.
     */
    public void displayRoleMenu(Role[] roles) {
        System.out.println("Seleziona il tipo di utenza:");

        for (Role role : roles) {
            // Escludiamo il ruolo tecnico di sistema dalla vista dell'utente
            if (role != Role.LOGIN) {
                System.out.println(role.getId() + ") " + role.getDescription());
            }
        }

        System.out.println("0) Esci");
        System.out.print("\nScelta: ");
    }

    public void promptEmail() {
        System.out.print("Email: ");
    }

    public void promptPassword() {
        System.out.print("Password: ");
    }

    public void displayError(String message) {
        System.out.println("\n[ERRORE] " + message + "\n");
    }

    public void displaySuccess(String nomeCompleto, Role role) {
        System.out.println("\n----------------------------------------");
        System.out.println("Accesso eseguito come: " + nomeCompleto);
        System.out.println("Ruolo: " + role.getDescription()); // Usiamo la descrizione per eleganza
        System.out.println("----------------------------------------\n");
    }

    public void displayGoodbye() {
        System.out.println("\nUscita dal modulo di Login. A presto!");
    }
}
