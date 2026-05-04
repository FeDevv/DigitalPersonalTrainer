package org.DPT.users.pt.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.catalog.esercizi.dao.ExerciseDAO;
import org.DPT.shared.catalog.macchinari.dao.MachineDAO;
import org.DPT.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.DPT.shared.workout.sheet.model.SheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.client.model.Client;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.pt.factory.PTUIFactory;
import org.DPT.users.pt.model.PT;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class PTLogicController {

    private final PTUI ui;
    private final AuthToken token;
    private final PT profile;

    private final PTDAO ptDAO = new PTDAO();
    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    public PTLogicController(Configuration config, Scanner scanner, AuthToken token,
                             ClientDAO clientDAO, WorkoutSheetDAO sheetDAO,
                             MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        this.ui = PTUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.clientDAO = clientDAO;
        this.sheetDAO = sheetDAO;
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

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
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void generateReport() {
        try {
            LocalDate start = ui.askForStartDate();
            LocalDate end = ui.askForEndDate();
            List<PTUI.PerformanceRecord> report = ptDAO.getPerformanceReport(profile.getId(), start, end);
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
