package org.DPT.users.receptionist.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO verticale per la gestione della tabella ASSEGNA.
 */
public class AssignmentDAO {

    /**
     * Crea una nuova assegnazione PT-Cliente tramite Stored Procedure.
     * La SP sp_assegna_pt gestirà automaticamente la disattivazione 
     * della vecchia assegnazione e l'inserimento della nuova.
     */
    public void createAssignment(int ptId, int clientId, int receptionistId) {
        String sql = "{CALL sp_assegna_pt(?, ?, ?)}";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (java.sql.CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, ptId);
            cstmt.setInt(2, clientId);
            cstmt.setInt(3, receptionistId);
            cstmt.execute();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Errore durante l'esecuzione della procedura di assegnazione PT-Cliente", e);
        }
    }
}
