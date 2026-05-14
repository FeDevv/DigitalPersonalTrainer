package org.dpt.user.owner.controller;

import org.dpt.auth.Role;
import org.dpt.domain.catalog.exercise.dto.ExerciseCreationDTO;
import org.dpt.domain.catalog.machine.dto.MachineCreationDTO;
import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.shared.mvc.AbstractCLIController;
import org.dpt.shared.utils.ValidationUtils;
import org.dpt.user.management.dto.ClientCreationDTO;
import org.dpt.user.management.dto.UserCreationDTO;
import org.dpt.domain.user.User;
import org.dpt.user.owner.view.OwnerCLIView;

import java.util.List;
import java.util.Scanner;

/**
 * Implementazione concreta dell'interfaccia OwnerUI per l'ambiente CLI.
 * 
 * Estende {@link AbstractCLIController} ereditando le primitive di acquisizione dati.
 * Gestisce l'interazione testuale specifica per il Proprietario, includendo la 
 * validazione dell'input per l'anagrafica staff e il catalogo tecnico.
 */
public class OwnerCLIController extends AbstractCLIController implements OwnerUI {

    private final OwnerCLIView ownerView;

    /**
     * Inizializza il controller UI associandogli la view specifica dell'Owner.
     * @param scanner Scanner condiviso per l'input.
     */
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

    /**
     * Acquisisce i dati per la registrazione di un macchinario tramite prompt testuali.
     */
    @Override
    public MachineCreationDTO askForMachineData() {
        String nome = readString("Nome Macchinario:");
        String desc = readString("Descrizione:");
        return new MachineCreationDTO(nome, desc);
    }

    /**
     * Guida l'utente nell'inserimento di un esercizio, gestendo l'associazione 
     * opzionale con i macchinari disponibili.
     */
    @Override
    public ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines) {
        String nome = readString("Nome Esercizio:");
        String desc = readString("Descrizione:");
        boolean corpoLibero = readString("È a corpo libero? (s/n):").equalsIgnoreCase("s");
        Integer machineId = null;
        if (!corpoLibero) {
            if (availableMachines.isEmpty()) {
                ownerView.displayError("Non risultano macchinari attivi in archivio. L'esercizio verrà marcato come 'Corpo Libero'.");
                corpoLibero = true;
            } else {
                ownerView.displayMachines(availableMachines);
                machineId = readInt("Inserisci l'identificativo (ID) del macchinario associato:");
            }
        }
        return new ExerciseCreationDTO(nome, desc, corpoLibero, machineId);
    }

    /**
     * Acquisisce i dati per un nuovo membro dello staff (PT o Segreteria).
     */
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
        // Vincolo architetturale: l'Owner non gestisce l'iscrizione diretta degli atleti.
        throw new UnsupportedOperationException("La registrazione dei Clienti è delegata esclusivamente alla Segreteria.");
    }

    @Override
    public int askForMachineIDToggle() { return readInt("Inserisci l'ID del Macchinario target:"); }

    @Override
    public int askForExerciseIDToggle() { return readInt("Inserisci l'ID dell'Esercizio target:"); }

    @Override
    public int askForUserID() { return readInt("Inserisci l'ID dell'utente target:"); }

    /**
     * Acquisisce il nuovo stato logico di un'entità.
     * @return true per 'Attivo', false per 'Disattivo'.
     */
    @Override
    public boolean askForNewStatus() {
        while (true) {
            int choice = readInt("Seleziona nuovo stato: (1) Attivo / (0) Disattivo:");
            if (choice == 1) return true;
            if (choice == 0) return false;
            ownerView.displayError("Input non valido: inserire '1' per attivare o '0' per disattivare.");
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
