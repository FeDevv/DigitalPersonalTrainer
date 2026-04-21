package org.DPT.login.model;

import org.DPT.auth.Role;

/**
 * Rappresenta l'esito positivo di un login.
 * Contiene le informazioni necessarie per stabilire la sessione successiva.
 */
public record LoginResult(int userId, Role role, String nomeCompleto) {
}
