package org.dpt.auth.login.model;

import org.dpt.auth.Role;

/**
 * Credenziale di sessione generata a seguito di un'autenticazione riuscita.
 * -
 * Questo record agisce come un "passaporto" interno: trasporta l'identità univoca 
 * dell'utente (userId) e il suo ruolo autorizzativo (role). Viene utilizzato 
 * dall'Orchestrator per determinare il modulo funzionale da caricare e dal 
 * DBConnectionManager per ricollegare la sessione con i privilegi DBMS corretti.
 * 
 * @param userId Identificativo univoco dell'utente nel database (PK).
 * @param role Il ruolo assegnato che definisce il perimetro operativo dell'utente.
 */
public record AuthToken(int userId, Role role) {
}
