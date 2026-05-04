package org.DPT.users.pt.factory;

import org.DPT.boot.model.UIMode;
import org.DPT.users.pt.controller.PTCLIController;
import org.DPT.users.pt.controller.PTUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Personal Trainer.
 */
public class PTUIFactory {

    public static PTUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new PTCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo PT.");
        };
    }
}
