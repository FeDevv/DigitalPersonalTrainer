package org.dpt.auth.login.controller;

import org.dpt.auth.Role;
import org.dpt.shared.mvc.AbstractCLIController;
import org.dpt.auth.login.view.LoginCLIView;

import java.util.Scanner;

/**
 * Implementazione concreta dell'interfaccia LoginUI per l'ambiente a riga di comando.
 * -
 * Estende {@link AbstractCLIController} per ereditare le logiche di acquisizione 
 * robusta dei dati e delega la renderizzazione visuale alla {@link LoginCLIView}.
 */
public class LoginCLIController extends AbstractCLIController implements LoginUI {

    private final LoginCLIView loginView;

    /**
     * Costruisce il controller UI associandogli una nuova istanza della view specifica.
     * @param scanner Riferimento allo scanner condiviso per l'input.
     */
    public LoginCLIController(Scanner scanner) {
        super(scanner, new LoginCLIView());
        this.loginView = (LoginCLIView) super.view;
    }

    @Override
    public void showHeader() {
        loginView.showLoginHeader();
    }

    @Override
    public void showMenu() {
        loginView.displayRoleMenu(Role.values());
    }

    @Override
    public int askForChoice() {
        return readInt("");
    }

    @Override
    public String askForEmail() {
        return readString("Email:");
    }

    @Override
    public String askForPassword() {
        return readString("Password:");
    }

    @Override
    public void reportGoodbye() {
        loginView.displayGoodbye();
    }
}
