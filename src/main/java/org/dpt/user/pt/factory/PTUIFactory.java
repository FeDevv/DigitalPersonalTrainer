package org.dpt.user.pt.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.pt.controller.PTCLIController;
import org.dpt.user.pt.controller.PTUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Personal Trainer.
 */
public class PTUIFactory {

    private PTUIFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PTUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new PTCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo PT.");
        };
    }
}
