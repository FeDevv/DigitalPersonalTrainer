package org.DPT.users.client.factory;

import org.DPT.boot.model.UIMode;
import org.DPT.users.client.controller.ClientCLIController;
import org.DPT.users.client.controller.ClientUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Cliente.
 */
public class ClientUIFactory {

    public static ClientUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new ClientCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per l'Area Atleta.");
        };
    }
}
