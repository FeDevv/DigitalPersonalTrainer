package org.DPT.users.login.controller;

import org.DPT.shared.auth.Role;
import org.DPT.shared.ui.BaseCLIController;
import org.DPT.users.login.view.LoginCLIView;

import java.util.Scanner;

/**
 * Implementazione CLI dell'interfaccia LoginUI.
 */
public class LoginCLIController extends BaseCLIController implements LoginUI {

    private final LoginCLIView loginView;

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
        return readInt("\n>> ");
    }

    @Override
    public String askForEmail() {
        return readString("Email");
    }

    @Override
    public String askForPassword() {
        return readString("Password");
    }

    @Override
    public void reportGoodbye() {
        loginView.displayGoodbye();
    }
}
