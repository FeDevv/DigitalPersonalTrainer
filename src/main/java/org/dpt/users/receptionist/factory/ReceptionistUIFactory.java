package org.dpt.users.receptionist.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.users.receptionist.controller.ReceptionistCLIController;
import org.dpt.users.receptionist.controller.ReceptionistUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Addetto Segreteria.
 */
public class ReceptionistUIFactory {

    public static ReceptionistUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new ReceptionistCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo Segreteria.");
        };
    }
}
