package org.dpt.users.pt.controller;

import org.dpt.boot.model.Configuration;
import org.dpt.exception.DatabaseException;
import org.dpt.shared.catalog.esercizi.dao.ExerciseDAO;
import org.dpt.shared.catalog.macchinari.dao.MachineDAO;
import org.dpt.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.shared.workout.sheet.model.SheetItem;
import org.dpt.shared.workout.sheet.model.WorkoutSheet;
import org.dpt.users.client.dao.ClientDAO;
import org.dpt.users.client.model.Client;
import org.dpt.users.login.model.AuthToken;
import org.dpt.users.pt.dao.PTDAO;
import org.dpt.users.pt.factory.PTUIFactory;
import org.dpt.users.pt.model.PT;
import org.dpt.users.pt.model.PerformanceDTO;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PTLogicController {

    private final PTUI ui;
    private final PT profile;

    private final PTDAO ptDAO;
    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    public PTLogicController(Configuration config, Scanner scanner, AuthToken token, Connection conn,
                             ClientDAO clientDAO, WorkoutSheetDAO sheetDAO,
                             MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        this.ui = PTUIFactory.getUI(config.uiMode(), scanner);
        this.clientDAO = clientDAO;
        this.sheetDAO = sheetDAO;
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        this.ptDAO = new PTDAO(conn);

        this.profile = ptDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo PT non trovato."));
    }

    public void execute() {
        ui.showHeader(profile.getFirstName());
        boolean logout = false;

        while (!logout) {
            ui.showMainMenu();
            int choice = ui.askForChoice();

            switch (choice) {
                case 1 -> createNewWorkoutSheet();
                case 2 -> viewSheetHistory();
                case 3 -> generateReport();
                case 4 -> viewCatalog();
                case 0 -> logout = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
        ui.reportGoodbye();
    }

    private void createNewWorkoutSheet() {
        try {
            List<Client> assignedClients = clientDAO.findAssignedToPT(profile.getId());
            if (assignedClients.isEmpty()) {
                ui.reportError("Non hai clienti assegnati attualmente.");
                return;
            }

            int clientId = ui.askForClientId(assignedClients);
            String title = ui.askForSheetTitle();

            // 1. Creazione record SCHEDA (tramite SP, Totale_Serie inizialmente 0)
            sheetDAO.createNewSheet(profile.getId(), clientId, title, 0);

            // 2. Recupero ID della scheda appena creata (quella attiva per il cliente)
            WorkoutSheet newSheet = sheetDAO.findActiveByClientId(clientId)
                    .orElseThrow(() -> new DatabaseException("Errore critico: scheda creata ma non trovata."));

            // 3. Composizione Esercizi (COMPOSTA)
            boolean adding = true;
            while (adding) {
                int exerciseId = ui.askForExerciseId(exerciseDAO.findAll(true));
                SheetItem details = ui.askForExerciseDetails(newSheet.id(), exerciseId);
                
                sheetDAO.addExerciseToSheet(
                        details.sheetId(),
                        details.exerciseId(),
                        details.restTime(),
                        details.executionNotes(),
                        details.expectedSets(),
                        details.expectedReps()
                );
                
                adding = ui.askIfAddAnotherExercise();
            }

            ui.reportSuccess("Scheda '" + title + "' creata e attivata con successo.");
        } catch (Exception e) {
            ui.reportError("Errore durante la creazione della scheda: " + e.getMessage());
        }
    }

    private void viewSheetHistory() {
        try {
            List<WorkoutSheet> history = sheetDAO.findByPTId(profile.getId());
            ui.showSheetHistory(history);
            
            if (!history.isEmpty()) {
                int sheetId = ui.askForID("Inserisci ID Scheda per i dettagli (0 per uscire):");
                if (sheetId != 0) {
                    history.stream()
                            .filter(s -> s.id() == sheetId)
                            .findFirst()
                            .ifPresentOrElse(
                                    s -> ui.showSheetDetails(s.title(), sheetDAO.getSheetDetails(sheetId)),
                                    () -> ui.reportError("ID non trovato nel tuo storico.")
                            );
                }
            }
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void generateReport() {
        try {
            LocalDate start = ui.askForStartDate();
            LocalDate end = ui.askForEndDate();
            List<PerformanceDTO> report = ptDAO.getPerformanceReport(profile.getId(), start, end);
            ui.showPerformanceReport(report);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void viewCatalog() {
        try {
            ui.showCatalog(machineDAO.getAll(), exerciseDAO.getAll());
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }
}
