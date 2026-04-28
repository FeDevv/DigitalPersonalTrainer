package org.DPT.shared.catalog.esercizi.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.catalog.esercizi.model.Exercise;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object per la gestione degli esercizi.
 */
public class ExerciseDAO {

    public Optional<Exercise> findById(int id) {
        String sql = "SELECT * FROM ESERCIZIO WHERE Codice_Esercizio = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExercise(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento dell'esercizio tramite ID: " + id, e);
        }
        return Optional.empty();
    }

    public List<Exercise> getAll() {
        return findByQuery("SELECT * FROM ESERCIZIO ORDER BY Nome", (Object[]) null);
    }

    public List<Exercise> findAll(boolean active) {
        return findByQuery("SELECT * FROM ESERCIZIO WHERE Esercizio_Attivo = ? ORDER BY Nome", active);
    }

    private List<Exercise> findByQuery(String sql, Object... params) {
        List<Exercise> list = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExercise(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'esecuzione della query di ricerca per esercizio", e);
        }
        return list;
    }

    public Exercise insert(org.DPT.shared.catalog.esercizi.dto.ExerciseCreationDTO data, int ownerId) {
        String sql = "INSERT INTO ESERCIZIO (ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo) VALUES (?, ?, ?, ?, ?, 1)";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ownerId);
            if (data.machineId() != null) {
                pstmt.setInt(2, data.machineId());
            } else {
                pstmt.setNull(2, Types.SMALLINT);
            }
            pstmt.setString(3, data.name());
            pstmt.setString(4, data.description());
            pstmt.setBoolean(5, data.isBodyweight());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Exercise(
                            generatedKeys.getInt(1),
                            ownerId,
                            data.machineId(),
                            data.name(),
                            data.description(),
                            data.isBodyweight(),
                            true
                    );
                } else {
                    throw new DatabaseException("Crezione dell'esercizio non riuscita: no ID generated.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento nel database dell'esercizio " + data.name(), e);
        }
    }

    public void updateStatus(int id, boolean active) {
        String sql = "UPDATE ESERCIZIO SET Esercizio_Attivo = ? WHERE Codice_Esercizio = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento dello stato dell'esercizio: " + id, e);
        }
    }

    private Exercise mapResultSetToExercise(ResultSet rs) throws SQLException {
        int mId = rs.getInt("ID_Macchinario");
        Integer machineId = rs.wasNull() ? null : mId;

        return new Exercise(
                rs.getInt("Codice_Esercizio"),
                rs.getInt("ID_Proprietario"),
                machineId,
                rs.getString("Nome"),
                rs.getString("Descrizione_Esercizio"),
                rs.getBoolean("Corpo_Libero"),
                rs.getBoolean("Esercizio_Attivo")
        );
    }
}
