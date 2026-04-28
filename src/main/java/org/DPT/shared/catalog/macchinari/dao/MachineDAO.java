package org.DPT.shared.catalog.macchinari.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.catalog.macchinari.model.Machine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object per la gestione dei macchinari.
 */
public class MachineDAO {

    public Optional<Machine> findById(int id) {
        String sql = "SELECT * FROM MACCHINARIO WHERE ID_Macchinario = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        return findByQuery("SELECT * FROM MACCHINARIO ORDER BY Nome", (Object[]) null);
    }

    public List<Machine> findAll(boolean active) {
        return findByQuery("SELECT * FROM MACCHINARIO WHERE Macchinario_Attivo = ? ORDER BY Nome", active);
    }

    private List<Machine> findByQuery(String sql, Object... params) {
        List<Machine> list = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public Machine insert(org.DPT.shared.catalog.macchinari.dto.MachineCreationDTO data, int ownerId) {
        String sql = "INSERT INTO MACCHINARIO (ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo) VALUES (?, ?, ?, 1)";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        String sql = "UPDATE MACCHINARIO SET Macchinario_Attivo = ? WHERE ID_Macchinario = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
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
