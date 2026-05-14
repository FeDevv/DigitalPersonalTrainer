package org.dpt.user.receptionist.view;

import org.dpt.auth.Role;
import org.dpt.shared.mvc.AbstractCLIView;

/**
 * Componente di visualizzazione per l'interfaccia a riga di comando del modulo Segreteria.
 * -
 * Seguendo il pattern MVC, questa classe ha la responsabilità esclusiva del 
 * rendering dei dati e della formattazione estetica dei menu. Non contiene logica 
 * decisionale, ma espone metodi atomici per la costruzione dell'output testuale.
 */
@SuppressWarnings("java:S106")
public class ReceptionistCLIView extends AbstractCLIView {

    /**
     * Visualizza l'intestazione personalizzata per l'operatore di segreteria.
     */
    public void displayReceptionistHeader(String name) {
        displayHeader("PANNELLO SEGRETERIA - Benvenuto/a " + name);
    }

    /**
     * Renderizza il menu principale delle funzionalità amministrative.
     */
    public void displayMainMenu() {
        displaySectionTitle("Menu Principale");
        displayLine("1. Gestione UTENZE (Staff/Clienti)");
        displayLine("2. Nuova Assegnazione PT-CLIENTE");
        displayLine("0. Logout");
    }

    /**
     * Menu di selezione per la categoria di utenti da amministrare.
     */
    public void displayUsersMenu() {
        displaySectionTitle("Gestione UTENZE");
        displayLine("1. Gestione PERSONAL TRAINER");
        displayLine("2. Gestione ADDETTI SEGRETERIA");
        displayLine("3. Gestione CLIENTI");
        displayLine("0. Torna indietro");
    }

    /**
     * Sottomenu dinamico per le azioni CRUD (Create, Read, Update status).
     * @param role Il ruolo corrente selezionato, per contestualizzare le etichette.
     */
    public void displayUserActionMenu(Role role) {
        displaySectionTitle("Azioni " + role);
        displayLine("1. Visualizza lista " + role.getPlural());
        displayLine("2. Attiva/Disattiva " + role.getSingular());
        displayLine("3. Inserisci nuovo " + role.getSingular());
        displayLine("0. Torna indietro");
    }

    public void displayGoodbye() {
        displayLine("\n Sessione Segretaria/o terminata. Arrivederci!");
    }
}
