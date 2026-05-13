package org.dpt.auth.login.view;

import org.dpt.auth.Role;
import org.dpt.shared.mvc.AbstractCLIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce l'output testuale per il modulo di Login.
 * Estende BaseCLIView per uniformità grafica.
 */
@SuppressWarnings("java:S106")
public class LoginCLIView extends AbstractCLIView {

    public void showLoginHeader() {
        displayHeader("AUTENTICAZIONE");
    }

    public void displayRoleMenu(Role[] roles) {
        displaySectionTitle("Seleziona il tipo di utenza");

        String[] headers = {"ID", "RUOLO"};
        List<String[]> rows = new ArrayList<>();
        
        for (Role role : roles) {
            if (role != Role.LOGIN) {
                rows.add(new String[]{
                    String.valueOf(role.getId()),
                    role.getSingular()
                });
            }
        }
        // opzione di uscita
        rows.add(new String[]{"0", "ESCI"});

        renderTable(headers, rows, new int[]{3, 20});
    }

    public void displayGoodbye() {
        displayLine("\n Uscita dal sistema. A presto!");
    }
}
