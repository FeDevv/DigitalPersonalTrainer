package org.dpt.user.owner.controller;

import org.dpt.domain.catalog.exercise.dto.ExerciseCreationDTO;
import org.dpt.domain.catalog.machine.dto.MachineCreationDTO;
import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.user.management.controller.UserManagementUI;

import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Proprietario.
 * Estende UserManagementUI per la gestione delle anagrafiche.
 */
public interface OwnerUI extends UserManagementUI {
    void showHeader(String ownerName);
    void showMainMenu();
    void showMachineMenu();
    void showExerciseMenu();
    
    // Metodi di Input aggregati tramite DTO (specifici per Owner)
    MachineCreationDTO askForMachineData();
    ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines);

    // Toggle stato (specifici per catalogo)
    int askForMachineIDToggle();
    int askForExerciseIDToggle();

    void showMachines(List<Machine> lista);
    void showExercises(List<Exercise> lista);

    void reportGoodbye();
}
