package org.dpt.boot.view;

import org.dpt.boot.model.UIMode;
import org.dpt.shared.mvc.AbstractCLIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente di visualizzazione CLI specializzato per la fase di avvio.
 * -
 * Estende {@link AbstractCLIView} per ereditare le capacità di rendering grafico 
 * e tabellare, focalizzandosi esclusivamente sulla presentazione dei messaggi di 
 * benvenuto e del menu di configurazione iniziale del sistema.
 */
@SuppressWarnings("java:S106")
public class BootCLIView extends AbstractCLIView {

    /**
     * Visualizza il banner di benvenuto e notifica l'inizio dell'inizializzazione.
     */
    public void displayWelcome() {
        displayWelcomeBanner();
        displayLine("Inizializzazione dei moduli core in corso...");
    }

    /**
     * Renderizza il menu di selezione della modalità UI utilizzando il toolkit tabellare.
     * @param modes Array di modalità disponibili acquisite dall'enum UIMode.
     */
    public void displayMenu(UIMode[] modes) {
        displaySectionTitle("Configurazione Ambiente di Esecuzione");
        
        String[] headers = {"ID", "MODALITÀ", "DESCRIZIONE TECNICA"};
        List<String[]> rows = new ArrayList<>();
        for (UIMode mode : modes) {
            rows.add(new String[]{
                String.valueOf(mode.getId()),
                mode.name(),
                mode.getDescription()
            });
        }
        
        renderTable(headers, rows, new int[]{3, 10, 42});
    }
}
