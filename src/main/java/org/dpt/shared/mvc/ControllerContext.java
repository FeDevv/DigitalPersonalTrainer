package org.dpt.shared.mvc;

import org.dpt.boot.model.Configuration;
import org.dpt.auth.login.model.AuthToken;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Oggetto di trasporto per il contesto di esecuzione dei LogicController.
 * -
 * Implementa il pattern "Parameter Object" per mitigare il code smell "Long Parameter List".
 * Aggrega in un unico record immutabile tutte le dipendenze ambientali e di stato
 * (configurazione globale, scanner I/O, token di sessione e connessione JDBC attiva)
 * necessarie per l'inizializzazione e l'operatività dei moduli funzionali.
 * -
 * Utilizza le potenzialità dei Java Records per garantire concisione e thread-safety 
 * attraverso l'immutabilità dei riferimenti.
 * 
 * @param config Configurazione globale dell'applicazione (es. UIMode).
 * @param scanner Riferimento allo scanner condiviso per l'input utente.
 * @param token Token di autenticazione contenente ID e Ruolo dell'utente loggato.
 * @param connection Connessione JDBC attiva con i privilegi RBAC corretti.
 */
public record ControllerContext(
        Configuration config,
        Scanner scanner,
        AuthToken token,
        Connection connection
) {}
