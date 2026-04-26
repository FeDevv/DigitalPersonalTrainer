package org.DPT.users.login.model;

import org.DPT.shared.auth.Role;

/**
 * Rappresenta l'esito positivo di un login (Auth Token).
 * Contiene l'ID univoco dell'utente e il suo ruolo per il dispacciamento nell'Orchestrator.
 */
public record AuthToken(int userId, Role role) {
}
