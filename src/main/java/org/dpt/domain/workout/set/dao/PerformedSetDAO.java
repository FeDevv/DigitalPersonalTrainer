package org.dpt.domain.workout.set.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.domain.workout.set.model.PerformedSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la gestione delle serie eseguite.
 * -
 * Gestisce la persistenza e l'aggiornamento in tempo reale delle singole serie.
 * Nota: l'aggiornamento della percentuale di completamento della sessione è
 * delegato a un trigger MariaDB ('trg_aggiorna_percentuale_update') che si attiva
 * automaticamente ad ogni modifica operata da questo DAO.
 */
public class PerformedSetDAO {
    private final Connection connection;

    private static final String FIND_ALL_BY_SESSION_ID = "SELECT ID_Sessione, Codice_Esercizio, Numero_Serie, Carico_Effettivo, Completata FROM SERIE_ESEGUITA WHERE ID_Sessione = ? ORDER BY Codice_Esercizio, Numero_Serie";
    private static final String UPDATE_PERFORMANCE = "UPDATE SERIE_ESEGUITA SET Carico_Effettivo = ?, Completata = ? " +
                     "WHERE ID_Sessione = ? AND Codice_Esercizio = ? AND Numero_Serie = ?";

    public PerformedSetDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Recupera tutte le serie associate a una specifica sessione.
     * @param sessionId ID della sessione di allenamento.
     * @return Lista di PerformedSet ordinata per esercizio e serie.
     */
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

    /**
     * Aggiorna i dati di performance (carico e completamento) di una singola serie.
     * 
     * @param sessionId Sessione di riferimento.
     * @param exerciseId Esercizio di riferimento.
     * @param setNumber Numero della serie.
     * @param weight Nuovo carico sollevato.
     * @param completed Nuovo stato di completamento.
     */
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
            throw new DatabaseException("Errore di aggiornamento della serie: " + e.getMessage(), e);
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
