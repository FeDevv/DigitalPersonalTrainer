package org.DPT.shared.workout.sheet.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO per le Schede di Allenamento.
 * Utilizza stored procedure per la creazione per garantire la coerenza della logica di business.
 */
public class WorkoutSheetDAO {

    /**
     * Recupera la routine dettagliata della scheda attualmente attiva per un cliente.
     * Interroga la vista 'vw_scheda_attiva_cliente'.
     */
    public List<ActiveSheetItem> getActiveRoutine(int clientId) {
        List<ActiveSheetItem> routine = new ArrayList<>();
        String sql = "SELECT * FROM vw_scheda_attiva_cliente WHERE ID_Cliente = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    routine.add(mapResultSetToActiveItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero della routine attiva per il cliente: " + clientId, e);
        }
        return routine;
    }

    /**
     * Recupera tutte le schede (attive e archiviate) di un cliente.
     */
    public List<WorkoutSheet> findAllByClientId(int clientId) {
        List<WorkoutSheet> history = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDA WHERE ID_Cliente = ? ORDER BY Data_Creazione DESC";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    /**
     * Recupera la scheda attualmente attiva per un cliente.
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
     * Crea una nuova scheda utilizzando la stored procedure del database.
     * Questa procedura disattiva automaticamente la scheda attiva precedente.
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
     * Aggiunge un esercizio a una scheda (tabella COMPOSTA).
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

    /**
     * Recupera tutte le schede redatte da uno specifico PT.
     */
    public List<WorkoutSheet> findByPTId(int ptId) {
        List<WorkoutSheet> sheets = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDA WHERE ID_PT = ? ORDER BY Data_Creazione DESC";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
