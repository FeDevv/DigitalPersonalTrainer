package org.dpt.user.receptionist.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Modello di dominio che rappresenta un Addetto alla Segreteria (Receptionist).
 * -
 * Estende {@link User} ereditandone le proprietà anagrafiche. Il Receptionist
 * è l'attore responsabile del front-office amministrativo, incaricato della 
 * gestione dell'anagrafica clienti e del coordinamento delle assegnazioni 
 * tra atleti e Personal Trainer.
 */
public class Receptionist extends User {
    /**
     * Costruisce un'istanza di Addetto Segreteria.
     * @param id Identificativo univoco.
     * @param firstName Nome dell'operatore.
     * @param lastName Cognome dell'operatore.
     * @param email Credenziale di accesso.
     * @param active Stato di operatività dell'utenza.
     */
    public Receptionist(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.RECEPTIONIST, active);
    }
}
