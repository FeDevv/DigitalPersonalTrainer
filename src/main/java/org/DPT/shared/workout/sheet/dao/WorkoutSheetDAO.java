package org.DPT.shared.workout.sheet.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;

import java.sql.*;
import java.util.Optional;

/**
 * DAO for Workout Sheets.
 * Uses stored procedures for creation to ensure business logic consistency.
 */
public class WorkoutSheetDAO {

    /**
     * Retrieves the currently active sheet for a client.
     */
    public Optional<WorkoutSheet> findActiveByClientId(int clientId) {
        String sql = "SELECT * FROM SCHEDA WHERE ID_Cliente = ? AND Scheda_Attiva = 1";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSheet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero della scheda attiva per il cliente: " + clientId, e);
        }
        return Optional.empty();
    }

    /**
     * Creates a new sheet using the database stored procedure.
     * This procedure automatically deactivates the previous active sheet.
     */
    public void createNewSheet(int ptId, int clientId, String title, int totalSets) {
        String sql = "{CALL sp_crea_nuova_scheda(?, ?, ?, ?)}";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, ptId);
            cstmt.setInt(2, clientId);
            cstmt.setString(3, title);
            cstmt.setInt(4, totalSets);

            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di creazione di scheda tramite stored procedure", e);
        }
    }

    /**
     * Adds an exercise to a sheet (COMPOSTA table).
     */
    public void addExerciseToSheet(int sheetId, int exerciseId, int rest, String notes, int sets, int reps) {
        String sql = "INSERT INTO COMPOSTA (ID_Scheda, Codice_Esercizio, Recupero, Note_Esecuzione, Serie_Previste, Ripetizioni_Previste) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sheetId);
            pstmt.setInt(2, exerciseId);
            pstmt.setInt(3, rest);
            pstmt.setString(4, notes);
            pstmt.setInt(5, sets);
            pstmt.setInt(6, reps);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiunta dell'esercizio alla scheda: " + sheetId, e);
        }
    }

    private WorkoutSheet mapResultSetToSheet(ResultSet rs) throws SQLException {
        return new WorkoutSheet(
                rs.getInt("ID_Scheda"),
                rs.getInt("ID_PT"),
                rs.getInt("ID_Cliente"),
                rs.getDate("Data_Creazione").toLocalDate(),
                rs.getString("Titolo"),
                rs.getBoolean("Scheda_Attiva"),
                rs.getInt("Totale_Serie_Previste")
        );
    }
}
