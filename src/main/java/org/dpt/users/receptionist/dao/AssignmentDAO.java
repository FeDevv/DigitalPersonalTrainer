package org.dpt.users.receptionist.dao;

import org.dpt.exception.DatabaseException;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * DAO verticale per la gestione della tabella ASSEGNA.
 */
public class AssignmentDAO {
    private final Connection connection;

    private static final String CREATE_ASSIGNMENT = "{CALL sp_assegna_pt(?, ?, ?)}";

    public AssignmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void createAssignment(int ptId, int clientId, int receptionistId) {
        try (CallableStatement cstmt = connection.prepareCall(CREATE_ASSIGNMENT)) {
            cstmt.setInt(1, ptId);
            cstmt.setInt(2, clientId);
            cstmt.setInt(3, receptionistId);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'esecuzione della procedura di assegnazione PT-Cliente", e);
        }
    }
}
