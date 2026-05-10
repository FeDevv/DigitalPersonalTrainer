package org.dpt.shared.workout.set.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.workout.set.model.PerformedSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la serie eseguita.
 */
public class PerformedSetDAO {
    private final Connection connection;

    private static final String FIND_ALL_BY_SESSION_ID = "SELECT ID_Sessione, Codice_Esercizio, Numero_Serie, Carico_Effettivo, Completata FROM SERIE_ESEGUITA WHERE ID_Sessione = ? ORDER BY Codice_Esercizio, Numero_Serie";
    private static final String UPDATE_PERFORMANCE = "UPDATE SERIE_ESEGUITA SET Carico_Effettivo = ?, Completata = ? " +
                     "WHERE ID_Sessione = ? AND Codice_Esercizio = ? AND Numero_Serie = ?";

    public PerformedSetDAO(Connection connection) {
        this.connection = connection;
    }

    public List<PerformedSet> findAllBySessionId(int sessionId) {
        List<PerformedSet> sets = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_ALL_BY_SESSION_ID)) {
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

    public void updatePerformance(int sessionId, int exerciseId, int setNumber, Double weight, boolean completed) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_PERFORMANCE)) {
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
