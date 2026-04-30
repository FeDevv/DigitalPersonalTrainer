package org.DPT.users.receptionist.factory;

import org.DPT.boot.model.UIMode;
import org.DPT.users.receptionist.controller.ReceptionistCLIController;
import org.DPT.users.receptionist.controller.ReceptionistUI;

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
