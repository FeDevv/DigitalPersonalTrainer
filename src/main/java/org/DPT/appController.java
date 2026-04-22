package org.DPT;

import org.DPT.login.controller.LoginCLIController;
import org.DPT.login.model.LoginResult;
import org.DPT.persistence.connection.DBConnectionManager;

import java.util.Scanner;

public class appController {

    public appController() {
        Scanner scanner = new Scanner(System.in);
    };

    public void run(){
        LoginCLIController loginController = new LoginCLIController();

        try {
            // Avvio del modulo di Login
            LoginResult session = loginController.start();

            if (session != null) {
                // In futuro qui chiameremo il dispatcher dei menu in base al ruolo
                System.out.println("DEBUG: Sessione avviata per " + session.nomeCompleto() + " (" + session.role() + ")");
                System.out.println("DEBUG: Il sistema è ora connesso con privilegi reali lato DB.");

                // Per ora, visto che non abbiamo ancora gli altri moduli, chiudiamo qui.
                System.out.println("\nLogica dei moduli successivi in fase di sviluppo...");
            }

        } catch (Exception e) {
            System.out.println("Errore fatale dell'applicazione: " + e.getMessage());
        } finally {
            // Pulizia risorse all'uscita
            DBConnectionManager.getInstance().closeConnection();
        }

    }



}
