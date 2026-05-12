package org.dpt.users.owner.controller;

import org.dpt.shared.auth.Role;
import org.dpt.shared.catalog.exercises.dto.ExerciseCreationDTO;
import org.dpt.shared.catalog.machinery.dto.MachineCreationDTO;
import org.dpt.shared.catalog.exercises.model.Exercise;
import org.dpt.shared.catalog.machinery.model.Machine;
import org.dpt.shared.ui.BaseCLIController;
import org.dpt.shared.utils.ValidationUtils;
import org.dpt.users.common.dto.ClientCreationDTO;
import org.dpt.users.common.dto.UserCreationDTO;
import org.dpt.users.common.model.User;
import org.dpt.users.owner.view.OwnerCLIView;

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
    public void showMachineMenu() { ownerView.displayMachineMenu(); }

    @Override
    public void showExerciseMenu() { ownerView.displayExercisesMenu(); }

    @Override
    public void showUsersMenu() { ownerView.displayUsersMenu(); }

    @Override
    public void showUserActionMenu(Role role) { ownerView.displayUserActionMenu(role); }

    @Override
    public int askForChoice() { return readInt(""); }

    // --- IMPLEMENTAZIONE INPUT AGGREGATI ---

    @Override
    public MachineCreationDTO askForMachineData() {
        String nome = readString("Nome Macchinario:");
        String desc = readString("Descrizione:");
        return new MachineCreationDTO(nome, desc);
    }

    @Override
    public ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines) {
        String nome = readString("Nome Esercizio:");
        String desc = readString("Descrizione:");
        boolean corpoLibero = readString("È a corpo libero? (s/n):").equalsIgnoreCase("s");
        Integer machineId = null;
        if (!corpoLibero) {
            if (availableMachines.isEmpty()) {
                ownerView.displayError("Non ci sono macchinari disponibili. L'esercizio verrà creato come corpo libero.");
                corpoLibero = true;
            } else {
                ownerView.displayMachines(availableMachines);
                machineId = readInt("Inserisci ID Macchinario associato:");
            }
        }
        return new ExerciseCreationDTO(nome, desc, corpoLibero, machineId);
    }

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
        // L'Owner non dovrebbe poter chiamare questo metodo grazie alla logica del controller,
        // ma lo implementiamo per completezza dell'interfaccia.
        throw new UnsupportedOperationException("I Clienti possono essere inseriti solo dalla Segreteria.");
    }

    // --- TOGGLE & STATO ---

    @Override
    public int askForMachineIDToggle() { return readInt("ID Macchinario da attivare/disattivare:"); }

    @Override
    public int askForExerciseIDToggle() { return readInt("ID Esercizio da attivare/disattivare:"); }

    @Override
    public int askForUserID() { return readInt("ID Utente da attivare/disattivare:"); }

    @Override
    public boolean askForNewStatus() {
        while (true) {
            int choice = readInt("Nuovo stato: (1) Attivo, (0) Disattivo:");
            if (choice == 1) return true;
            if (choice == 0) return false;
            ownerView.displayError("Inserisci solo '1' per Attivo o '0' per Disattivo.\n");
        }
    }

    @Override
    public void showMachines(List<Machine> lista) { ownerView.displayMachines(lista); }

    @Override
    public void showExercises(List<Exercise> lista) { ownerView.displayExercises(lista); }

    @Override
    public void showUsers(List<? extends User> lista, String titolo) { ownerView.renderUserTable(lista, titolo); }

    @Override
    public void reportSuccess(String message) { ownerView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { ownerView.displayGoodbye(); }
}
