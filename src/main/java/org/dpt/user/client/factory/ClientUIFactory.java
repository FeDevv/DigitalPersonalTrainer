package org.dpt.user.client.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.client.controller.ClientCLIController;
import org.dpt.user.client.controller.ClientUI;

import java.util.Scanner;

/**
 * Factory specializzata per l'istanziazione delle implementazioni UI dell'Area Cliente.
 * -
 * Implementa il pattern <b>Simple Factory</b> per separare la creazione della 
 * componente View dalla logica del controller. Permette al sistema di evolvere 
 * verso una GUI mantenendo invariato il nucleo del modulo Cliente.
 */
public class ClientUIFactory {

    private ClientUIFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Restituisce l'implementazione UI corretta basata sulla configurazione globale.
     * 
     * @param mode Modalità di interfaccia (CLI/GUI).
     * @param scanner Scanner di sistema per l'input testuale.
     * @return Un'istanza di {@link ClientUI}.
     * @throws UnsupportedOperationException Se la modalità richiesta non è ancora implementata.
     */
    public static ClientUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new ClientCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per l'Area Cliente.");
        };
    }
}
