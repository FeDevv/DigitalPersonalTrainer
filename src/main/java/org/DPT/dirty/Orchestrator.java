package org.DPT.dirty;

import org.DPT.boot.controller.BootLogicController;
import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;
import org.DPT.users.login.controller.LoginLogicController;
import org.DPT.users.login.model.LoginResult;
import org.DPT.connection.DBConnectionManager;
import org.DPT.dirty.proprietario.controller.ProprietarioController;

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
            BootLogicController bootController = new BootLogicController(sharedScanner);
            Configuration config = bootController.execute(args);

            // Per ora blocchiamo l'avvio se si tenta di usare la GUI (da implementare)
            if (config.uiMode() == UIMode.GUI) {
                System.out.println("\n[AVVISO] Interfaccia Grafica non ancora implementata. Avviare in modalità CLI.");
                return;
            }

            // ==========================================
            // FASE 2: AUTENTICAZIONE
            // ==========================================
            LoginLogicController loginController = new LoginLogicController(config, sharedScanner);
            LoginResult sessionToken= loginController.execute();

            // Se l'utente ha premuto '0' per uscire
            if (sessionToken == null) {
                return;
            }

            // ==========================================
            // FASE 3: ROUTING (Dispatcher dei ruoli)
            // ==========================================
            dispatch(sessionToken, sharedScanner);

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
     * Indirizza l'utente al modulo corretto in base al suo ruolo.
     * @param sessionToken Informazioni della sessione corrente.
     */
    private void dispatch(LoginResult sessionToken, Scanner sharedScanner) {
        switch (sessionToken.role()) {
            case PROPRIETARIO -> {
                ProprietarioController controller = new ProprietarioController(sharedScanner, sessionToken);
                controller.execute();
            }
            case PT -> System.out.println("\n[INFO] Modulo Personal Trainer in arrivo...");
            case SEGRETERIA -> System.out.println("\n[INFO] Modulo Segreteria in arrivo...");
            case CLIENTE -> System.out.println("\n[INFO] Modulo Cliente in arrivo...");
            default -> System.out.println("\n[ERRORE] Ruolo non riconosciuto per il dispatching.");
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
