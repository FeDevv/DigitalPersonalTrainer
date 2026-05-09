package org.dpt.users.login.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.users.login.controller.LoginCLIController;
import org.dpt.users.login.controller.LoginUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo Login.
 * Decide quale implementazione di LoginUI istanziare in base alla configurazione.
 */
public class LoginUIFactory {

    public static LoginUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new LoginCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("GUI non ancora implementata per il Login.");
        };
    }
}
