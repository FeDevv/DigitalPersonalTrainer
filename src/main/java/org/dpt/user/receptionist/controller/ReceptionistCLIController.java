package org.dpt.user.receptionist.controller;

import org.dpt.auth.Role;
import org.dpt.shared.mvc.AbstractCLIController;
import org.dpt.shared.utils.ValidationUtils;
import org.dpt.user.management.dto.ClientCreationDTO;
import org.dpt.user.management.dto.UserCreationDTO;
import org.dpt.domain.user.User;
import org.dpt.user.receptionist.view.ReceptionistCLIView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ReceptionistCLIController extends AbstractCLIController implements ReceptionistUI {

    private final ReceptionistCLIView recView;

    public ReceptionistCLIController(Scanner scanner) {
        super(scanner, new ReceptionistCLIView());
        this.recView = (ReceptionistCLIView) super.view;
    }

    @Override
    public void showHeader(String name) { recView.displayReceptionistHeader(name); }

    @Override
    public void showMainMenu() { recView.displayMainMenu(); }

    @Override
    public void showUsersMenu() { recView.displayUsersMenu(); }

    @Override
    public void showUserActionMenu(Role role) { recView.displayUserActionMenu(role); }

    @Override
    public int askForChoice() { return readInt(""); }

    @Override
    public UserCreationDTO askForStaffData() {
        String nome = readString("Nome:");
        String cognome = readString("Cognome:");
        String email = ValidationUtils.validateEmail(readString("Email:"));
        String pass = readString("Password:");
        return new UserCreationDTO(nome, cognome, email, pass);
    }

    @Override
    public ClientCreationDTO askForClientData() {
        UserCreationDTO base = askForStaffData();
        String cf = readString("Codice Fiscale:");
        String ind = readString("Indirizzo:");
        LocalDate data = null;
        while (data == null) {
            try {
                data = LocalDate.parse(readString("Data di Nascita (AAAA-MM-GG):"));
            } catch (DateTimeParseException _) {
                recView.displayError("Formato data non valido.");
            }
        }
        return new ClientCreationDTO(base, cf, ind, data);
    }

    @Override
    public int askForUserID() { return readInt("ID Utente da attivare/disattivare:"); }

    @Override
    public boolean askForNewStatus() {
        while (true) {
            int choice = readInt("Nuovo stato: (1) Attivo, (0) Disattivo:");
            if (choice == 1) return true;
            if (choice == 0) return false;
            recView.displayError("Inserisci solo '1' per Attivo o '0' per Disattivo.\n");
        }
    }

    @Override
    public int askForPTId() { return readInt("ID del Personal Trainer da assegnare:"); }

    @Override
    public int askForClientId() { return readInt("ID del Cliente:"); }

    @Override
    public void showUsers(List<? extends User> lista, String titolo) { recView.renderUserTable(lista, titolo); }

    @Override
    public void reportSuccess(String message) { recView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { recView.displayGoodbye(); }
}
