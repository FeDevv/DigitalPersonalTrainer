package org.DPT.users.owner.controller;

import org.DPT.shared.catalog.esercizi.dto.ExerciseCreationDTO;
import org.DPT.shared.catalog.macchinari.dto.MachineCreationDTO;
import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.model.User;

import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Proprietario.
 */
public interface OwnerUI {
    void showHeader(String ownerName);
    void showMainMenu();
    void showMacchinariMenu();
    void showEserciziMenu();
    void showUtenzeMenu();
    void showUtenzaActionMenu(String tipo);
    
    int askForChoice();
    
    // Metodi di Input aggregati tramite DTO
    MachineCreationDTO askForMachineData();
    ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines);
    UserCreationDTO askForStaffData();

    // Toggle stato
    int askForIDMacchinarioDaToggle();
    int askForIDEsercizioDaToggle();
    int askForIDUtente();
    boolean askForNewStatus();

    void showMacchinari(List<Machine> lista);
    void showEsercizi(List<Exercise> lista);
    void showUtenti(List<? extends User> lista, String titolo);

    void reportError(String message);
    void reportSuccess(String message);
    void reportGoodbye();
}
