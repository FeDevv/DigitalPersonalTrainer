package org.DPT.users.pt.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.users.pt.model.PT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PTDAO {
    public Optional<PT> findById(int id) {
        String sql = "SELECT ID_PT, Nome, Cognome, Email, PT_Attivo FROM PT WHERE ID_PT = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPT(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamente del profilo del PT", e);
        }
        return Optional.empty();
    }

    public List<PT> getAll() {
        return findByQuery("SELECT * FROM PT ORDER BY Cognome, Nome", (Object[]) null);
    }

    public List<PT> findAll(boolean active) {
        return findByQuery("SELECT * FROM PT WHERE PT_Attivo = ? ORDER BY Cognome, Nome", active);
    }

    private List<PT> findByQuery(String sql, Object... params) {
        List<PT> list = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPT(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore d'esecuzione della query di ricerca per PT", e);
        }
        return list;
    }

    private PT mapResultSetToPT(ResultSet rs) throws SQLException {
        return new PT(
                rs.getInt("ID_PT"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                rs.getBoolean("PT_Attivo")
        );
    }

    public void activate(int id) { updateStatus(id, true); }
    public void deactivate(int id) { updateStatus(id, false); }

    public void insert(org.DPT.users.common.dto.UserCreationDTO data) {
        String sql = "INSERT INTO PT (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.firstName());
            pstmt.setString(2, data.lastName());
            pstmt.setString(3, data.email());
            pstmt.setString(4, data.password());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento del nuovo PT: " + data.email(), e);
        }
    }

    public void updateStatus(int id, boolean active) {
        String sql = "UPDATE PT SET PT_Attivo = ? WHERE ID_PT = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di aggiornamento dello stato del PT", e);
        }
    }
}
