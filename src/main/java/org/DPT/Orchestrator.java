package org.DPT;

import org.DPT.boot.controller.BootController;
import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;
import org.DPT.login.controller.LoginController;
import org.DPT.login.model.LoginResult;
import org.DPT.persistence.connection.DBConnectionManager;

import java.util.Scanner;

/**
 * Cuore dell'applicazione.
 * Gestisce il ciclo di vita globale, la condivisione delle risorse (Scanner, DB)
 * e la transizione di stato tra i vari moduli (Boot -> Login -> Dashboard).
 */
public class Orchestrator {

    private final Scanner sharedScanner;

    public Orchestrator() {
        // L'UNICO punto in tutto il progetto in cui viene aperto lo stream di sistema.
        this.sharedScanner = new Scanner(System.in);
    }

    /**
     * Avvia la catena di esecuzione dell'applicazione.
     * @param args Argomenti da riga di comando passati dal Main.
     */
    public void run(String[] args) {
        try {
            // ==========================================
            // FASE 1: BOOTSTRAP (Configurazione iniziale)
            // ==========================================
            BootController bootController = new BootController(sharedScanner);
            Configuration config = bootController.execute(args);

            // Per ora blocchiamo l'avvio se si tenta di usare la GUI (da implementare)
            if (config.uiMode() == UIMode.GUI) {
                System.out.println("\n[AVVISO] Interfaccia Grafica non ancora implementata. Avviare in modalità CLI.");
                return;
            }

            // ==========================================
            // FASE 2: AUTENTICAZIONE
            // ==========================================
            LoginController loginController = new LoginController(config, sharedScanner);
            LoginResult session = loginController.execute();

            // Se l'utente ha premuto '0' per uscire
            if (session == null) {
                return;
            }

            // ==========================================
            // FASE 3: ROUTING (Dispatcher dei ruoli)
            // ==========================================
            System.out.println("DEBUG: Routing in corso per " + session.nomeCompleto() + "...");
            System.out.println("DEBUG: Connessione DB attiva con permessi blindati per ruolo: " + session.role());

            // Qui in futuro faremo uno switch sul ruolo per lanciare i moduli specifici
            // es: if (session.role() == Role.PT) { new PTController(sharedScanner, session).execute(); }

            System.out.println("\nLogica dei moduli successivi in fase di sviluppo...");

        } catch (Exception e) {
            // Gestione degli errori fatali non intercettati dai sottomoduli
            System.err.println("\n[ERRORE FATALE DI SISTEMA] " + e.getMessage());
            e.printStackTrace(); // Utile in fase di sviluppo
        } finally {
            // ==========================================
            // FASE 4: TEARDOWN (Pulizia Risorse)
            // ==========================================
            shutDown();
        }
    }

    /**
     * Procedura di spegnimento sicuro.
     * Garantisce che non ci siano memory leak o connessioni appese su MariaDB.
     */
    private void shutDown() {
        System.out.println("\nChiusura dell'applicazione in corso...");

        DBConnectionManager.getInstance().closeConnection();

        if (sharedScanner != null) {
            sharedScanner.close();
        }

        System.out.println("Risorse liberate. Arrivederci.");
    }
}
