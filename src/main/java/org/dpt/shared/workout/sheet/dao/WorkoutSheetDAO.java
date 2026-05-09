package org.dpt.shared.workout.sheet.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.workout.sheet.model.ActiveSheetItem;
import org.dpt.shared.workout.sheet.model.WorkoutSheet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO per le Schede di Allenamento.
 */
public class WorkoutSheetDAO {
    private final Connection connection;

    private static final String ACTIVE_ROUTINE = "SELECT * FROM vw_scheda_attiva_cliente WHERE ID_Cliente = ?";
    private static final String SHEET_DETAILS = """
            SELECT s.ID_Cliente, s.ID_Scheda, s.Titolo as Nome_Scheda,
                c.Codice_Esercizio, e.Nome as Nome_Esercizio,
                c.Serie_Previste, c.Ripetizioni_Previste, c.Recupero,
                c.Note_Esecuzione, e.Corpo_Libero
            FROM SCHEDA s
            JOIN COMPOSTA c ON s.ID_Scheda = c.ID_Scheda
            JOIN ESERCIZIO e ON c.Codice_Esercizio = e.Codice_Esercizio
            WHERE s.ID_Scheda = ?
            """;
    private static final String FIND_ALL_BY_CLIENT_ID = "SELECT * FROM SCHEDA WHERE ID_Cliente = ? ORDER BY Data_Creazione DESC";
    private static final String FIND_ACTIVE_BY_CLIENT_ID = "SELECT * FROM SCHEDA WHERE ID_Cliente = ? AND Scheda_Attiva = 1";
    private static final String CREATE_NEW_SHEET = "{CALL sp_crea_nuova_scheda(?, ?, ?, ?)}";
    private static final String ADD_EXERCISE_TO_SHEET = "INSERT INTO COMPOSTA (ID_Scheda, Codice_Esercizio, Recupero, Note_Esecuzione, Serie_Previste, Ripetizioni_Previste) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_PT_ID = "SELECT * FROM SCHEDA WHERE ID_PT = ? ORDER BY Data_Creazione DESC";

    public WorkoutSheetDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ActiveSheetItem> getActiveRoutine(int clientId) {
        return getRoutineByQuery(ACTIVE_ROUTINE, clientId);
    }

    public List<ActiveSheetItem> getSheetDetails(int sheetId) {
        return getRoutineByQuery(SHEET_DETAILS, sheetId);
    }

    private List<ActiveSheetItem> getRoutineByQuery(String sql, int id) {
        List<ActiveSheetItem> routine = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    routine.add(mapResultSetToActiveItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero della routine per ID: " + id, e);
        }
        return routine;
    }

    public List<WorkoutSheet> findAllByClientId(int clientId) {
        List<WorkoutSheet> history = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_ALL_BY_CLIENT_ID)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    history.add(mapResultSetToSheet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero dello storico schede per il cliente: " + clientId, e);
        }
        return history;
    }

    public Optional<WorkoutSheet> findActiveByClientId(int clientId) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_ACTIVE_BY_CLIENT_ID)) {
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

    public void createNewSheet(int ptId, int clientId, String title, int totalSets) {
        try (CallableStatement cstmt = connection.prepareCall(CREATE_NEW_SHEET)) {
            cstmt.setInt(1, ptId);
            cstmt.setInt(2, clientId);
            cstmt.setString(3, title);
            cstmt.setInt(4, totalSets);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di creazione di scheda tramite stored procedure", e);
        }
    }

    public void addExerciseToSheet(int sheetId, int exerciseId, int rest, String notes, int sets, int reps) {
        try (PreparedStatement pstmt = connection.prepareStatement(ADD_EXERCISE_TO_SHEET)) {
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

    public List<WorkoutSheet> findByPTId(int ptId) {
        List<WorkoutSheet> sheets = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_PT_ID)) {
            pstmt.setInt(1, ptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sheets.add(mapResultSetToSheet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero dello storico schede per il PT: " + ptId, e);
        }
        return sheets;
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

    private ActiveSheetItem mapResultSetToActiveItem(ResultSet rs) throws SQLException {
        return new ActiveSheetItem(
                rs.getInt("ID_Cliente"),
                rs.getInt("ID_Scheda"),
                rs.getString("Nome_Scheda"),
                rs.getInt("Codice_Esercizio"),
                rs.getString("Nome_Esercizio"),
                rs.getInt("Serie_Previste"),
                rs.getInt("Ripetizioni_Previste"),
                rs.getInt("Recupero"),
                rs.getString("Note_Esecuzione"),
                rs.getBoolean("Corpo_Libero")
        );
    }
}
