package org.dpt.user.receptionist.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.receptionist.controller.ReceptionistCLIController;
import org.dpt.user.receptionist.controller.ReceptionistUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Addetto Segreteria.
 */
public class ReceptionistUIFactory {

    private ReceptionistUIFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static ReceptionistUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new ReceptionistCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo Segreteria.");
        };
    }
}
