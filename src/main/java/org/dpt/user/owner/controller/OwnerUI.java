package org.dpt.user.owner.controller;

import org.dpt.domain.catalog.exercise.dto.ExerciseCreationDTO;
import org.dpt.domain.catalog.machine.dto.MachineCreationDTO;
import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.user.management.controller.UserManagementUI;

import java.util.List;

/**
 * Contratto di astrazione per l'interfaccia utente dedicata al modulo Proprietario.
 * -
 * Estende {@link UserManagementUI} ereditando le capacità di amministrazione utenti,
 * e aggiunge le specifiche operazioni di I/O necessarie per il controllo del 
 * catalogo tecnico (Esercizi e Macchinari).
 */
public interface OwnerUI extends UserManagementUI {
    /** Visualizza l'header con il nome del proprietario loggato. */
    void showHeader(String ownerName);
    
    /** Mostra il menu principale con le macro-aree (Macchinari, Esercizi, Staff). */
    void showMainMenu();
    
    /** Mostra il sottomenu dedicato all'anagrafica macchinari. */
    void showMachineMenu();
    
    /** Mostra il sottomenu dedicato al catalogo esercizi. */
    void showExerciseMenu();
    
    /** Acquisisce i dati tecnici per la registrazione di un macchinario. */
    MachineCreationDTO askForMachineData();
    
    /** 
     * Acquisisce i dati per un nuovo esercizio, fornendo la lista dei macchinari
     * disponibili per garantire la coerenza referenziale lato UI.
     */
    ExerciseCreationDTO askForExerciseData(List<Machine> availableMachines);

    /** Richiede l'ID del macchinario su cui operare il toggle di stato. */
    int askForMachineIDToggle();
    
    /** Richiede l'ID dell'esercizio su cui operare il toggle di visibilità. */
    int askForExerciseIDToggle();

    /** Renderizza la lista tabellare dei macchinari. */
    void showMachines(List<Machine> lista);
    
    /** Renderizza la lista tabellare degli esercizi. */
    void showExercises(List<Exercise> lista);

    /** Visualizza il messaggio di chiusura sessione. */
    void reportGoodbye();
}
