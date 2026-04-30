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
     * Crea una nuova assegnazione PT-Cliente.
     * Il trigger trg_disattiva_assegnazione_precedente nel DB gestirà
     * automaticamente la disattivazione della vecchia assegnazione.
     */
    public void createAssignment(int ptId, int clientId, int receptionistId) {
        String sql = "INSERT INTO ASSEGNA (ID_PT, ID_Cliente, ID_Addetto) VALUES (?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ptId);
            pstmt.setInt(2, clientId);
            pstmt.setInt(3, receptionistId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la creazione dell'assegnazione PT-Cliente", e);
        }
    }
}
