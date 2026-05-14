package org.dpt.user.pt.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.pt.controller.PTCLIController;
import org.dpt.user.pt.controller.PTUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo funzionale del Personal Trainer.
 * -
 * Implementa il pattern "Simple Factory" per garantire il disaccoppiamento tra 
 * la logica di controllo tecnico e l'infrastruttura di presentazione. 
 * Gestisce l'istanziazione delle implementazioni di {@link PTUI} basandosi 
 * sulla modalità UI selezionata (CLI/GUI).
 */
public class PTUIFactory {

    /** Impedisce l'istanziazione esterna (Utility Class). */
    private PTUIFactory() {
        throw new IllegalStateException("Utility class: non istanziabile.");
    }

    /**
     * Risolve e istanzia l'implementazione UI per il Personal Trainer.
     * @param mode Modalità determinata a runtime (CLI o GUI).
     * @param scanner Riferimento allo scanner per la modalità CLI.
     * @return L'istanza concreta di PTUI.
     */
    public static PTUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new PTCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("Ingegneria: l'interfaccia grafica per il modulo PT non è attualmente disponibile.");
        };
    }
}
