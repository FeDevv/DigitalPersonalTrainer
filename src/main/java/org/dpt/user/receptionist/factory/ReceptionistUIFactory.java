package org.dpt.user.receptionist.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.receptionist.controller.ReceptionistCLIController;
import org.dpt.user.receptionist.controller.ReceptionistUI;

import java.util.Scanner;

/**
 * Factory specializzata per l'istanziazione delle implementazioni UI del modulo Segreteria.
 * -
 * Implementa il pattern <b>Simple Factory</b> per centralizzare la logica di 
 * creazione della componente View/Interfaccia, garantendo che il controller logico 
 * non sia accoppiato a una specifica tecnologia di input/output.
 */
public class ReceptionistUIFactory {

    private ReceptionistUIFactory() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Restituisce l'istanza corretta di ReceptionistUI in base alla configurazione di sistema.
     * 
     * @param mode Modalità di interfaccia richiesta (CLI o GUI).
     * @param scanner Scanner per l'input (necessario per CLI).
     * @return Un'implementazione concreta di {@link ReceptionistUI}.
     * @throws UnsupportedOperationException Se la modalità richiesta non è ancora supportata.
     */
    public static ReceptionistUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new ReceptionistCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo Segreteria.");
        };
    }
}
