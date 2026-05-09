package org.dpt.shared.catalog.esercizi.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.shared.catalog.esercizi.model.Exercise;
import org.dpt.shared.catalog.esercizi.dto.ExerciseCreationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object per la gestione degli esercizi.
 */
public class ExerciseDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT * FROM ESERCIZIO WHERE Codice_Esercizio = ?";
    private static final String SELECT_ALL = "SELECT * FROM ESERCIZIO ORDER BY Codice_Esercizio";
    private static final String FIND_ALL_BY_STATUS = "SELECT * FROM ESERCIZIO WHERE Esercizio_Attivo = ? ORDER BY Codice_Esercizio";
    private static final String INSERT_EXERCISE = "INSERT INTO ESERCIZIO (ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo) VALUES (?, ?, ?, ?, ?, 1)";
    private static final String UPDATE_STATUS = "UPDATE ESERCIZIO SET Esercizio_Attivo = ? WHERE Codice_Esercizio = ?";

    public ExerciseDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Exercise> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
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
        return findByQuery(SELECT_ALL, null);
    }

    public List<Exercise> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    private List<Exercise> findByQuery(String sql, Object[] params) {
        List<Exercise> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

    public Exercise insert(ExerciseCreationDTO data, int ownerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_EXERCISE, Statement.RETURN_GENERATED_KEYS)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Esercizio con ID " + id + " non trovato.");
            }
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
