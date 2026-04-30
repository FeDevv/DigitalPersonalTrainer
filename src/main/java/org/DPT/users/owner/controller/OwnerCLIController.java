package org.DPT.users.owner.controller;

import org.DPT.shared.catalog.esercizi.dto.ExerciseCreationDTO;
import org.DPT.shared.catalog.macchinari.dto.MachineCreationDTO;
import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.ui.BaseCLIController;
import org.DPT.shared.utils.ValidationUtils;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.model.User;
import org.DPT.users.owner.view.OwnerCLIView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class OwnerCLIController extends BaseCLIController implements OwnerUI {

    private final OwnerCLIView ownerView;

    public OwnerCLIController(Scanner scanner) {
        super(scanner, new OwnerCLIView());
        this.ownerView = (OwnerCLIView) super.view;
    }

    @Override
    public void showHeader(String ownerName) { ownerView.displayOwnerHeader(ownerName); }

    @Override
    public void showMainMenu() { ownerView.displayMainMenu(); }

    @Override
    public void showMacchinariMenu() { ownerView.displayMacchinariMenu(); }

    @Override
    public void showEserciziMenu() { ownerView.displayEserciziMenu(); }

    @Override
    public void showUtenzeMenu() { ownerView.displayUtenzeMenu(); }

    @Override
    public void showUtenzaActionMenu(String tipo) { ownerView.displayUtenzaActionMenu(tipo); }

    @Override
    public int askForChoice() { return readInt("\n>> "); }

    // --- IMPLEMENTAZIONE INPUT AGGREGATI ---

    @Override
    public MachineCreationDTO askForMachineData() {
        String nome = readString("Nome Macchinario");
        String desc = readString("Descrizione");
        return new MachineCreationDTO(nome, desc);
    }

    @Override
    public ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines) {
        String nome = readString("Nome Esercizio");
        String desc = readString("Descrizione");
        boolean corpoLibero = readString("È a corpo libero? (s/n)").equalsIgnoreCase("s");
        Integer machineId = null;
        if (!corpoLibero) {
            if (availableMachines.isEmpty()) {
                ownerView.displayError("Non ci sono macchinari disponibili. L'esercizio verrà creato come corpo libero.");
                corpoLibero = true;
            } else {
                ownerView.displayMacchinari(availableMachines);
                machineId = readInt("Inserisci ID Macchinario associato");
            }
        }
        return new ExerciseCreationDTO(nome, desc, corpoLibero, machineId);
    }

    @Override
    public UserCreationDTO askForStaffData() {
        String nome = readString("Nome");
        String cognome = readString("Cognome");
        String email = ValidationUtils.validateEmail(readString("Email"));
        String pass = readString("Password");
        return new UserCreationDTO(nome, cognome, email, pass);
    }

    // --- TOGGLE & STATO ---

    @Override
    public int askForIDMacchinarioDaToggle() { return readInt("ID Macchinario da attivare/disattivare"); }

    @Override
    public int askForIDEsercizioDaToggle() { return readInt("ID Esercizio da attivare/disattivare"); }

    @Override
    public int askForIDUtente() { return readInt("ID Utente da attivare/disattivare"); }

    @Override
    public boolean askForNewStatus() {
        String choice = readString("Nuovo stato: (1) Attivo, (0) Disattivo");
        return choice.equals("1");
    }

    @Override
    public void showMacchinari(List<Machine> lista) { ownerView.displayMacchinari(lista); }

    @Override
    public void showEsercizi(List<Exercise> lista) { ownerView.displayEsercizi(lista); }

    @Override
    public void showUtenti(List<? extends User> lista, String titolo) { ownerView.displayUtenti(lista, titolo); }

    @Override
    public void reportSuccess(String message) { ownerView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { ownerView.displayGoodbye(); }
}
