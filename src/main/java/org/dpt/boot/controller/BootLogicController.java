package org.dpt.boot.controller;

import org.dpt.boot.model.Configuration;
import org.dpt.boot.model.UIMode;

import java.util.Locale;
import java.util.Scanner;

/**
 * Controller logico responsabile della fase di Bootstrapping dell'applicazione.
 * -
 * Il suo compito è determinare le impostazioni ambientali iniziali prima che 
 * l'Orchestrator prenda il controllo del ciclo di vita della sessione.
 * Gestisce il parsing dei parametri da riga di comando (es. flag --gui o --cli)
 * e, in assenza di questi, attiva una procedura interattiva di configurazione.
 */
public class BootLogicController {

    /** Delegato per la gestione dell'interazione UI durante il boot. */
    private final BootCLIController cliController;

    /**
     * Inizializza il controller di boot collegandolo allo scanner di sistema.
     * @param sharedScanner Riferimento allo scanner unico dell'applicazione.
     */
    public BootLogicController(Scanner sharedScanner) {
        this.cliController = new BootCLIController(sharedScanner);
    }

    /**
     * Esegue l'algoritmo di inizializzazione della configurazione.
     * -
     * Il flusso segue una gerarchia di precedenza:
     * <ol>
     *   <li>Parametri CLI (Flag espliciti).</li>
     *   <li>Interazione guidata (Menu di scelta).</li>
     * </ol>
     * 
     * @param args Array di stringhe passate al metodo main.
     * @return Oggetto Configuration immutabile contenente le impostazioni di avvio.
     */
    public Configuration execute(String[] args) {
        // Verifica dei parametri passati all'avvio (Precedenza Massima)
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--gui")) return Configuration.defaultGUI();
                if (arg.equalsIgnoreCase("--cli")) return Configuration.defaultCLI();
            }
        }

        // Procedura interattiva (Fallback)
        cliController.showWelcome();
        cliController.showMenu();
        
        UIMode selectedMode = null;
        while (selectedMode == null) {
            int inputId = cliController.askForChoice();
            selectedMode = UIMode.getModeFromId(inputId);

            if (selectedMode == null) {
                cliController.reportError("Configurazione: l'identificativo '" + inputId + "' non è mappato a nessuna modalità UI supportata.");
            }
        }

        return new Configuration(selectedMode, Locale.ITALY);
    }
}
