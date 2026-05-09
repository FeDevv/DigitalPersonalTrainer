package org.dpt.users.owner.controller;

import org.dpt.shared.catalog.esercizi.dto.ExerciseCreationDTO;
import org.dpt.shared.catalog.macchinari.dto.MachineCreationDTO;
import org.dpt.shared.catalog.esercizi.model.Exercise;
import org.dpt.shared.catalog.macchinari.model.Machine;
import org.dpt.users.common.controller.UserManagementUI;

import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Proprietario.
 * Estende UserManagementUI per la gestione delle anagrafiche.
 */
public interface OwnerUI extends UserManagementUI {
    void showHeader(String ownerName);
    void showMainMenu();
    void showMacchinariMenu();
    void showEserciziMenu();
    
    // Metodi di Input aggregati tramite DTO (specifici per Owner)
    MachineCreationDTO askForMachineData();
    ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines);

    // Toggle stato (specifici per catalogo)
    int askForIDMacchinarioDaToggle();
    int askForIDEsercizioDaToggle();

    void showMacchinari(List<Machine> lista);
    void showEsercizi(List<Exercise> lista);

    void reportGoodbye();
}
