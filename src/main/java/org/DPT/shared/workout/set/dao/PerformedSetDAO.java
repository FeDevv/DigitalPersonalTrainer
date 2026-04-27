package org.DPT.shared.workout.set.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.workout.set.model.PerformedSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Performed Sets.
 * Manages the atomic progress of a workout session.
 */
public class PerformedSetDAO {

    /**
     * Retrieves all sets for a given session.
     */
    public List<PerformedSet> findAllBySessionId(int sessionId) {
        List<PerformedSet> sets = new ArrayList<>();
        String sql = "SELECT * FROM SERIE_ESEGUITA WHERE ID_Sessione = ? ORDER BY Codice_Esercizio, Numero_Serie";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sessionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sets.add(mapResultSetToSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero delle serie per la sessione: " + sessionId, e);
        }
        return sets;
    }

    /**
     * Updates a specific set performance.
     * Note: Database trigger 'trg_aggiorna_percentuale_update' will automatically update the session percentage.
     */
    public void updatePerformance(int sessionId, int exerciseId, int setNumber, Double weight, boolean completed) {
        String sql = "UPDATE SERIE_ESEGUITA SET Carico_Effettivo = ?, Completata = ? " +
                     "WHERE ID_Sessione = ? AND Codice_Esercizio = ? AND Numero_Serie = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (weight != null) {
                pstmt.setDouble(1, weight);
            } else {
                pstmt.setNull(1, java.sql.Types.DECIMAL);
            }
            pstmt.setBoolean(2, completed);
            pstmt.setInt(3, sessionId);
            pstmt.setInt(4, exerciseId);
            pstmt.setInt(5, setNumber);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di aggiornamento della serie", e);
        }
    }

    private PerformedSet mapResultSetToSet(ResultSet rs) throws SQLException {
        double w = rs.getDouble("Carico_Effettivo");
        Double weight = rs.wasNull() ? null : w;

        return new PerformedSet(
                rs.getInt("ID_Sessione"),
                rs.getInt("Codice_Esercizio"),
                rs.getInt("Numero_Serie"),
                weight,
                rs.getBoolean("Completata")
        );
    }
}
