package org.dpt.user.pt.controller;

import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.SheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.model.Client;
import org.dpt.user.pt.model.PerformanceDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Contratto di astrazione per l'interfaccia utente dedicata al Personal Trainer.
 * -
 * Definisce i metodi di I/O per la gestione dei workout programmati, la selezione 
 * assistita dei clienti assegnati e la composizione interattiva delle schede 
 * di allenamento con i relativi parametri tecnici.
 */
public interface PTUI {
    /** Visualizza l'header con il nome del PT loggato. */
    void showHeader(String ptName);
    
    /** Mostra il menu principale del modulo (Schede, Storico, Report, Catalogo). */
    void showMainMenu();
    
    /** Acquisisce la scelta numerica dell'utente dai menu. */
    int askForChoice();

    /** Renderizza la lista dei soli atleti assegnati al PT corrente. */
    void showAssignedClients(List<Client> clients);
    
    /** Richiede l'identificativo del cliente scelto per una nuova scheda. */
    int askForClientId(List<Client> availableClients);
    
    /** Richiede l'inserimento del titolo descrittivo della scheda. */
    String askForSheetTitle();
    
    /** Visualizza gli esercizi attivi filtrati dal catalogo. */
    void showExerciseCatalog(List<Exercise> exercises);
    
    /** Richiede l'ID dell'esercizio da aggiungere alla scheda. */
    int askForExerciseId(List<Exercise> availableExercises);
    
    /** Acquisisce i parametri tecnici (recupero, serie, rep, note) per un esercizio. */
    SheetItem askForExerciseDetails(int sheetId, int exerciseId);
    
    /** Domanda all'utente se desidera inserire ulteriori esercizi nel piano. */
    boolean askIfAddAnotherExercise();

    /** Renderizza la cronologia delle schede redatte dal PT. */
    void showSheetHistory(List<WorkoutSheet> sheets);
    
    /** Visualizza il dettaglio esercizi di una specifica scheda. */
    void showSheetDetails(String title, List<ActiveSheetItem> details);
    
    /** Mostra una vista unificata di macchinari ed esercizi del centro. */
    void showCatalog(List<Machine> machines, List<Exercise> exercises);
    
    /** Richiede un identificativo generico con prompt personalizzato. */
    int askForID(String prompt);

    /** Acquisisce la data di inizio per la reportistica. */
    LocalDate askForStartDate();
    
    /** Acquisisce la data di fine per la reportistica. */
    LocalDate askForEndDate();
    
    /** Renderizza il report tabellare delle prestazioni atleti. */
    void showPerformanceReport(List<PerformanceDTO> report);

    /** Notifica un errore funzionale. */
    void reportError(String message);
    
    /** Notifica il successo di una procedura (es. salvataggio scheda). */
    void reportSuccess(String message);
    
    /** Visualizza il messaggio di chiusura sessione PT. */
    void reportGoodbye();
}
