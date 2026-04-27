package org.DPT.users.receptionist.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.users.receptionist.model.Receptionist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReceptionistDAO {
    public Optional<Receptionist> findById(int id) {
        String sql = "SELECT ID_Addetto, Nome, Cognome, Email, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE ID_Addetto = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReceptionist(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del profilo dell'addetto di segreteria", e);
        }
        return Optional.empty();
    }

    public List<Receptionist> getAll() {
        return findByQuery("SELECT * FROM ADDETTO_SEGRETERIA ORDER BY Cognome, Nome", (Object[]) null);
    }

    public List<Receptionist> findAll(boolean active) {
        return findByQuery("SELECT * FROM ADDETTO_SEGRETERIA WHERE Addetto_Attivo = ? ORDER BY Cognome, Nome", active);
    }

    private List<Receptionist> findByQuery(String sql, Object... params) {
        List<Receptionist> list = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReceptionist(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore d'esecuzione della query di ricerca per l'addetto di segreteria", e);
        }
        return list;
    }

    private Receptionist mapResultSetToReceptionist(ResultSet rs) throws SQLException {
        return new Receptionist(
                rs.getInt("ID_Addetto"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                rs.getBoolean("Addetto_Attivo")
        );
    }

    public void activate(int id) { updateStatus(id, true); }
    public void deactivate(int id) { updateStatus(id, false); }

    private void updateStatus(int id, boolean active) {
        String sql = "UPDATE ADDETTO_SEGRETERIA SET Addetto_Attivo = ? WHERE ID_Addetto = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore d'aggiornamento dello stato dell'addetto di segreteria", e);
        }
    }
}
