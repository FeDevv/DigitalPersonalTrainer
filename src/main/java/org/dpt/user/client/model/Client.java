package org.dpt.user.client.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Modello di dominio che rappresenta un Cliente del sistema.
 * -
 * Estende l'entità base {@link User} specializzandola con informazioni anagrafiche
 * dettagliate tramite composizione con {@link ClientPersonalInfo}.
 * Questa struttura permette di mantenere atomica la gestione dell'account utente
 * separandola dai dati di natura amministrativa/fiscale del cliente.
 */
public class Client extends User {
    private final ClientPersonalInfo personalInfo;

    /**
     * Costruisce un'istanza completa di Cliente.
     * 
     * @param id Identificativo univoco del cliente.
     * @param firstName Nome del cliente.
     * @param lastName Cognome del cliente.
     * @param email Indirizzo email (login univoco).
     * @param personalInfo Record contenente i dati sensibili e fiscali.
     * @param active Stato di attivazione del profilo.
     */
    public Client(int id, String firstName, String lastName, String email, 
                  ClientPersonalInfo personalInfo, boolean active) {
        super(id, firstName, lastName, email, Role.CLIENT, active);
        this.personalInfo = personalInfo;
    }

    public String getFiscalCode() { return personalInfo.fiscalCode(); }
    public String getAddress() { return personalInfo.address(); }
    public java.time.LocalDate getBirthDate() { return personalInfo.birthDate(); }
}
