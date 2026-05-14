package org.dpt.user.pt.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Modello di dominio che rappresenta un Personal Trainer (PT).
 * -
 * Estende {@link User} ereditandone le proprietà anagrafiche. Il PT agisce 
 * come l'istruttore responsabile della programmazione tecnica per un insieme 
 * di atleti assegnati, con privilegi di scrittura sul catalogo delle schede 
 * e di lettura sui report delle prestazioni.
 */
public class PT extends User {
    /**
     * Costruisce un'istanza di Personal Trainer.
     * @param id Identificativo univoco.
     * @param firstName Nome dell'istruttore.
     * @param lastName Cognome dell'istruttore.
     * @param email Credenziale di accesso.
     * @param active Stato di operatività nel centro sportivo.
     */
    public PT(int id, String firstName, String lastName, String email, boolean active) {
        super(id, firstName, lastName, email, Role.PT, active);
    }
}
