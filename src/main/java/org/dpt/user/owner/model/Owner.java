package org.dpt.user.owner.model;

import org.dpt.auth.Role;
import org.dpt.domain.user.User;

/**
 * Modello di dominio che rappresenta il Proprietario (Owner) della palestra.
 * -
 * Estende {@link User} ereditandone le proprietà anagrafiche e di sicurezza.
 * All'interno del sistema, il Proprietario è considerato l'utente con il massimo
 * livello di privilegio (Root), abilitato alla gestione completa degli asset 
 * fisici e delle risorse umane.
 */
public class Owner extends User {
    /**
     * Costruisce un'istanza di Owner.
     * @param id Identificativo univoco del database.
     * @param firstName Nome del proprietario.
     * @param lastName Cognome del proprietario.
     * @param email Indirizzo di contatto e login.
     */
    public Owner(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email, Role.OWNER, true);
    }
}
