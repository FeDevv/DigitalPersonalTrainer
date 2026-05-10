package org.dpt.shared.context;

import org.dpt.boot.model.Configuration;
import org.dpt.users.login.model.AuthToken;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Raggruppa i parametri di contesto necessari ai LogicController.
 * Risolve il code smell "Too many parameters" aggregando le dipendenze ambientali.
 */
public record ControllerContext(
        Configuration config,
        Scanner scanner,
        AuthToken token,
        Connection connection
) {}
