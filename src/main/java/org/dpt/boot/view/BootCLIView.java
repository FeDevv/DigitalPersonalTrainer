package org.dpt.boot.view;

import org.dpt.boot.model.UIMode;
import org.dpt.shared.ui.BaseCLIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce ESCLUSIVAMENTE l'output a schermo per il boot.
 */
@SuppressWarnings("java:S106")
public class BootCLIView extends BaseCLIView {

    public void displayWelcome() {
        displayWelcomeBanner();
        displayLine("Inizializzazione sistema in corso...");
    }

    public void displayMenu(UIMode[] modes) {
        displaySectionTitle("Configurazione Interfaccia");
        
        String[] headers = {"ID", "MODALITÀ", "DESCRIZIONE"};
        List<String[]> rows = new ArrayList<>();
        for (UIMode mode : modes) {
            rows.add(new String[]{
                String.valueOf(mode.getId()),
                mode.name(),
                mode.getDescription()
            });
        }
        
        renderTable(headers, rows, new int[]{3, 10, 30});
    }
}
