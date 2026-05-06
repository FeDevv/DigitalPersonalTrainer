package org.DPT.users.login.view;

import org.DPT.shared.auth.Role;
import org.DPT.shared.ui.BaseCLIView;

import org.DPT.shared.auth.Role;
import org.DPT.shared.ui.BaseCLIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce ESCLUSIVAMENTE l'output testuale per il modulo di Login.
 * Estende BaseCLIView per uniformità grafica.
 */
public class LoginCLIView extends BaseCLIView {

    public void showLoginHeader() {
        displayHeader("AUTENTICAZIONE");
    }

    public void displayRoleMenu(Role[] roles) {
        displaySectionTitle("Seleziona il tipo di utenza");

        String[] headers = {"ID", "RUOLO", "DESCRIZIONE"};
        List<String[]> rows = new ArrayList<>();
        
        for (Role role : roles) {
            if (role != Role.LOGIN) {
                rows.add(new String[]{
                    String.valueOf(role.getId()),
                    role.name(),
                    role.getDescription()
                });
            }
        }
        // Aggiungiamo l'opzione di uscita
        rows.add(new String[]{"0", "EXIT", "Chiudi applicazione"});

        renderTable(headers, rows, new int[]{3, 15, 35});
    }

    public void displayGoodbye() {
        displayLine("\n Uscita dal sistema. A presto!");
    }
}
