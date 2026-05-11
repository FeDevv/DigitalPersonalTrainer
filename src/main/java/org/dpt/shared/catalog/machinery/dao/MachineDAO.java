package org.dpt.shared.catalog.machinery.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.shared.catalog.machinery.model.Machine;
import org.dpt.shared.catalog.machinery.dto.MachineCreationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object per la gestione dei macchinari.
 */
public class MachineDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_Macchinario, ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo FROM MACCHINARIO WHERE ID_Macchinario = ?";
    private static final String SELECT_ALL = "SELECT ID_Macchinario, ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo FROM MACCHINARIO ORDER BY ID_Macchinario";
    private static final String FIND_ALL_BY_STATUS = "SELECT ID_Macchinario, ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo FROM MACCHINARIO WHERE Macchinario_Attivo = ? ORDER BY ID_Macchinario";
    private static final String INSERT_MACHINE = "INSERT INTO MACCHINARIO (ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo) VALUES (?, ?, ?, 1)";
    private static final String UPDATE_STATUS = "UPDATE MACCHINARIO SET Macchinario_Attivo = ? WHERE ID_Macchinario = ?";

    public MachineDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Machine> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMachine(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del macchinario tramite ID: " + id, e);
        }
        return Optional.empty();
    }

    public List<Machine> getAll() {
        return findByQuery(SELECT_ALL, null);
    }

    public List<Machine> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    private List<Machine> findByQuery(String sql, Object[] params) {
        List<Machine> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMachine(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'esecuzione della query di ricerca per macchinario", e);
        }
        return list;
    }

    public Machine insert(MachineCreationDTO data, int ownerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_MACHINE, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ownerId);
            pstmt.setString(2, data.name());
            pstmt.setString(3, data.description());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Machine(
                            generatedKeys.getInt(1),
                            ownerId,
                            data.name(),
                            data.description(),
                            true
                    );
                } else {
                    throw new DatabaseException("Creazione del macchinario non riuscita: no ID generated.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento del macchinario: " + data.name(), e);
        }
    }

    public void updateStatus(int id, boolean active) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Macchinario con ID " + id + " non trovato.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento dello stato del macchinario: " + id, e);
        }
    }

    private Machine mapResultSetToMachine(ResultSet rs) throws SQLException {
        return new Machine(
                rs.getInt("ID_Macchinario"),
                rs.getInt("ID_Proprietario"),
                rs.getString("Nome"),
                rs.getString("Descrizione_Macchinario"),
                rs.getBoolean("Macchinario_Attivo")
        );
    }
}
