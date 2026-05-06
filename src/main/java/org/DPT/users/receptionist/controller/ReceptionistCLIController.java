package org.DPT.users.receptionist.controller;

import org.DPT.shared.ui.BaseCLIController;
import org.DPT.shared.utils.ValidationUtils;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.model.User;
import org.DPT.users.receptionist.view.ReceptionistCLIView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ReceptionistCLIController extends BaseCLIController implements ReceptionistUI {

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
    public void showUtenzeMenu() { recView.displayUtenzeMenu(); }

    @Override
    public void showUtenzaActionMenu(String tipo) { recView.displayUtenzaActionMenu(tipo); }

    @Override
    public int askForChoice() { return readInt(""); }

    @Override
    public UserCreationDTO askForStaffData() {
        String nome = readString("Nome: ");
        String cognome = readString("Cognome: ");
        String email = ValidationUtils.validateEmail(readString("Email: "));
        String pass = readString("Password: ");
        return new UserCreationDTO(nome, cognome, email, pass);
    }

    @Override
    public ClientCreationDTO askForClientData() {
        UserCreationDTO base = askForStaffData();
        String cf = readString("Codice Fiscale: ");
        String ind = readString("Indirizzo: ");
        LocalDate data = null;
        while (data == null) {
            try {
                data = LocalDate.parse(readString("Data di Nascita (AAAA-MM-GG): "));
            } catch (DateTimeParseException e) {
                recView.displayError("Formato data non valido.");
            }
        }
        return new ClientCreationDTO(base, cf, ind, data);
    }

    @Override
    public int askForIDUtente() { return readInt("ID Utente da attivare/disattivare: "); }

    @Override
    public boolean askForNewStatus() {
        while (true) {
            int choice = readInt("Nuovo stato: (1) Attivo, (0) Disattivo: ");
            if (choice == 1) return true;
            if (choice == 0) return false;
            recView.displayError("Inserisci solo '1' per Attivo o '0' per Disattivo.\n");
        }
    }

    @Override
    public int askForPTId() { return readInt("ID del Personal Trainer da assegnare: "); }

    @Override
    public int askForClientId() { return readInt("ID del Cliente: "); }

    @Override
    public void showUtenti(List<? extends User> lista, String titolo) { recView.displayUtenti(lista, titolo); }

    @Override
    public void reportSuccess(String message) { recView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { recView.displayGoodbye(); }
}
