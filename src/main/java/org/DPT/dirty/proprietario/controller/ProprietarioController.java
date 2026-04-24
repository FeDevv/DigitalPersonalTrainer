package org.DPT.dirty.proprietario.controller;

import org.DPT.users.login.model.LoginResult;
import org.DPT.dirty.proprietario.persistence.ProprietarioDAO;
import org.DPT.dirty.shared.catalogo.persistence.CatalogoDAO;
import org.DPT.exception.DatabaseException;

import java.util.Scanner;

/**
 * Controller Logico principale per il modulo Proprietario.
 * Gestisce il flusso decisionale e il wiring tra Interfaccia e Persistenza.
 */
public class ProprietarioController {

    private final Scanner sharedScanner;
    private final LoginResult sessionToken;
    private final ProprietarioCLIController cliController;
    private final CatalogoDAO catalogoDAO;
    private final ProprietarioDAO proprietarioDAO;

    public ProprietarioController(Scanner sharedScanner, LoginResult sessionToken) {
        this.sharedScanner = sharedScanner;
        this.sessionToken = sessionToken;
        this.cliController = new ProprietarioCLIController(sharedScanner);
        this.catalogoDAO = new CatalogoDAO();
        this.proprietarioDAO = new ProprietarioDAO();
    }

    public void execute() {
        cliController.showHeader();
        boolean logout = false;

        while (!logout) {
            cliController.showMainMenu();
            int choice = cliController.askForChoice();

            switch (choice) {
                case 1 -> manageMacchinari();
                case 2 -> manageEsercizi();
                case 3 -> manageUtenze();
                case 0 -> {
                    cliController.reportGoodbye();
                    logout = true;
                }
                default -> cliController.reportError("Scelta non valida. Riprova.");
            }
        }
    }

    private void manageMacchinari() {
        boolean back = false;
        while (!back) {
            cliController.showMacchinariMenu();
            int choice = cliController.askForChoice();

            try {
                switch (choice) {
                    case 1 -> cliController.showMacchinari(catalogoDAO.getAllMacchinari());
                    case 2 -> {
                        String nome = cliController.askForNome();
                        String desc = cliController.askForDescrizione();
                        proprietarioDAO.insertMacchinario(sessionToken.userId(), nome, desc);
                        cliController.reportSuccess("Macchinario inserito con successo.");
                    }
                    case 3 -> {
                        int id = cliController.askForIDMacchinarioDaDisattivare();
                        proprietarioDAO.toggleStatoMacchinario(id);
                        cliController.reportSuccess("Stato macchinario aggiornato.");
                    }
                    case 0 -> back = true;
                    default -> cliController.reportError("Scelta non valida.");
                }
            } catch (DatabaseException e) {
                cliController.reportError(e.getMessage());
            }
        }
    }

    private void manageEsercizi() {
        boolean back = false;
        while (!back) {
            cliController.showEserciziMenu();
            int choice = cliController.askForChoice();

            try {
                switch (choice) {
                    case 1 -> cliController.showEsercizi(catalogoDAO.getAllEsercizi());
                    case 2 -> {
                        String nome = cliController.askForNome();
                        String desc = cliController.askForDescrizione();
                        int idMacch = cliController.askForIDMacchinario();
                        
                        boolean corpoLibero = (idMacch == 0);
                        Integer macchParam = corpoLibero ? null : idMacch;
                        
                        proprietarioDAO.insertEsercizio(sessionToken.userId(), nome, desc, corpoLibero, macchParam);
                        cliController.reportSuccess("Esercizio inserito con successo.");
                    }
                    case 3 -> {
                        int id = cliController.askForIDEsercizio();
                        proprietarioDAO.toggleStatoEsercizio(id);
                        cliController.reportSuccess("Stato esercizio aggiornato.");
                    }
                    case 0 -> back = true;
                    default -> cliController.reportError("Scelta non valida.");
                }
            } catch (DatabaseException e) {
                cliController.reportError(e.getMessage());
            }
        }
    }

    private void manageUtenze() {
        boolean back = false;
        while (!back) {
            cliController.showUtenzeMenu();
            int choice = cliController.askForChoice();

            switch (choice) {
                case 1 -> manageUtenzaSpecifica("ADDETTO");
                case 2 -> manageUtenzaSpecifica("PT");
                case 3 -> manageUtenzaSpecifica("CLIENTE");
                case 0 -> back = true;
                default -> cliController.reportError("Scelta non valida.");
            }
        }
    }

    /**
     * Helper per gestire l'inserimento/disattivazione di utenze staff e clienti.
     */
    private void manageUtenzaSpecifica(String tipo) {
        cliController.showUtenzaActionMenu(tipo);

        int choice = cliController.askForChoice();
        try {
            switch (choice) {
                case 1 -> {
                    switch (tipo) {
                        case "PT" -> cliController.showUtenti(proprietarioDAO.getAllPT(), "PERSONAL TRAINER");
                        case "ADDETTO" -> cliController.showUtenti(proprietarioDAO.getAllAddetti(), "ADDETTI SEGRETERIA");
                        case "CLIENTE" -> cliController.showUtenti(proprietarioDAO.getAllClienti(), "CLIENTI");
                    }
                }
                case 2 -> {
                    String nome = cliController.askForNomePersona();
                    String cognome = cliController.askForCognome();
                    String email = cliController.askForEmail();
                    String pass = cliController.askForPassword();

                    switch (tipo) {
                        case "PT" -> proprietarioDAO.insertPT(nome, cognome, email, pass);
                        case "ADDETTO" -> proprietarioDAO.insertAddetto(nome, cognome, email, pass);
                        case "CLIENTE" -> {
                            String cf = cliController.askForCF();
                            String ind = cliController.askForIndirizzo();
                            String dataStr = cliController.askForDataNascita();
                            proprietarioDAO.insertCliente(nome, cognome, email, pass, cf, ind, java.sql.Date.valueOf(dataStr));
                        }
                    }
                    cliController.reportSuccess(tipo + " inserito con successo.");
                }
                case 3 -> {
                    int id = cliController.askForIDUtente();
                    switch (tipo) {
                        case "PT" -> proprietarioDAO.toggleStatoPT(id);
                        case "ADDETTO" -> proprietarioDAO.toggleStatoAddetto(id);
                        case "CLIENTE" -> proprietarioDAO.toggleStatoCliente(id);
                    }
                    cliController.reportSuccess("Stato " + tipo + " aggiornato.");
                }
                case 0 -> { /* Torna indietro */ }
                default -> cliController.reportError("Scelta non valida.");
            }
        } catch (DatabaseException e) {
            cliController.reportError(e.getMessage());
        } catch (IllegalArgumentException e) {
            cliController.reportError("Formato dati non valido (es. data).");
        }
    }
}
