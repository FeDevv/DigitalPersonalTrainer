package org.DPT.boot.controller;

import org.DPT.boot.model.UIMode;
import org.DPT.boot.view.BootCLIView;
import org.DPT.shared.ui.BaseCLIController;

import java.util.Scanner;

/**
 * Gestisce il flusso dell'interfaccia CLI per il boot.
 */
public class BootCLIController extends BaseCLIController {

    private final BootCLIView bootView;

    public BootCLIController(Scanner scanner) {
        super(scanner, new BootCLIView());
        this.bootView = (BootCLIView) super.view;
    }

    public void showWelcome() {
        bootView.displayWelcome();
    }

    public void showMenu() {
        bootView.displayMenu(UIMode.values());
    }

    public int askForChoice() {
        return readInt("\n>> ");
    }
}
