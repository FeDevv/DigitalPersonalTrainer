package org.DPT.users.login.view;

import org.DPT.shared.auth.Role;
import org.DPT.shared.ui.BaseCLIView;

/**
 * Gestisce ESCLUSIVAMENTE l'output testuale per il modulo di Login.
 * Estende BaseCLIView per uniformità grafica.
 */
public class LoginCLIView extends BaseCLIView {

    public void showLoginHeader() {
        displayHeader("LOGIN");
    }

    public void displayRoleMenu(Role[] roles) {
        displaySectionTitle("Seleziona il tipo di utenza");

        for (Role role : roles) {
            if (role != Role.LOGIN) {
                displayLine(role.getId() + ") " + role.getDescription());
            }
        }
        displayLine("0) Esci");
    }

    public void displayLoginSuccess(String nomeCompleto, Role role) {
        displayLine("\n----------------------------------------");
        displayLine("Accesso eseguito come: " + nomeCompleto);
        displayLine("Ruolo: " + role.getDescription());
        displayLine("----------------------------------------\n");
    }

    public void displayGoodbye() {
        displayLine("\nUscita dal modulo di Login. A presto!");
    }
}
