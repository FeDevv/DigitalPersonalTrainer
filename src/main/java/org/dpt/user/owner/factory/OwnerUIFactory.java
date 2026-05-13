package org.dpt.user.owner.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.owner.controller.OwnerCLIController;
import org.dpt.user.owner.controller.OwnerUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Proprietario.
 * Decide quale implementazione di OwnerUI istanziare in base alla configurazione.
 */
public class OwnerUIFactory {

    private OwnerUIFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static OwnerUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new OwnerCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il modulo Proprietario.");
        };
    }
}
