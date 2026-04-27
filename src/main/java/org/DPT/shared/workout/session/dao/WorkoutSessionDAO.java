package org.DPT.shared.workout.session.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.workout.session.model.WorkoutSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Workout Sessions.
 * Handles the lifecycle of a training session.
 */
public class WorkoutSessionDAO {

    /**
     * Starts a new session. 
     * Note: Database trigger 'trg_popola_serie_sessione' will automatically populate SERIE_ESEGUITA.
     */
    public WorkoutSession startSession(int sheetId) {
        String sql = "INSERT INTO SESSIONE (ID_Scheda, Data, Ora_Inizio, Percentuale_Completamento) VALUES (?, ?, ?, 0)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        LocalDate now = LocalDate.now();
        LocalTime startTime = LocalTime.now();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sheetId);
            pstmt.setDate(2, Date.valueOf(now));
            pstmt.setTime(3, Time.valueOf(startTime));

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new WorkoutSession(
                            generatedKeys.getInt(1),
                            sheetId,
                            now,
                            startTime,
                            null,
                            0
                    );
                } else {
                    throw new DatabaseException("Impossibile avviare la sessione: no ID generated.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di avvio della sessione per la scheda: " + sheetId, e);
        }
    }

    /**
     * Closes a session by setting the end time.
     */
    public void endSession(int sessionId) {
        String sql = "UPDATE SESSIONE SET Ora_Fine = ? WHERE ID_Sessione = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTime(1, Time.valueOf(LocalTime.now()));
            pstmt.setInt(2, sessionId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di chiusura della sessione: " + sessionId, e);
        }
    }

    public List<WorkoutSession> findAllBySheetId(int sheetId) {
        List<WorkoutSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM SESSIONE WHERE ID_Scheda = ? ORDER BY Data DESC, Ora_Inizio DESC";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sheetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero della sessione per la scheda: " + sheetId, e);
        }
        return sessions;
    }

    private WorkoutSession mapResultSetToSession(ResultSet rs) throws SQLException {
        Time endTime = rs.getTime("Ora_Fine");
        return new WorkoutSession(
                rs.getInt("ID_Sessione"),
                rs.getInt("ID_Scheda"),
                rs.getDate("Data").toLocalDate(),
                rs.getTime("Ora_Inizio").toLocalTime(),
                endTime != null ? endTime.toLocalTime() : null,
                rs.getInt("Percentuale_Completamento")
        );
    }
}
