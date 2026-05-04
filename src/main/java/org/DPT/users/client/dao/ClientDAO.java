package org.DPT.users.client.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.users.client.model.Client;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {
    public Optional<Client> findById(int id) {
        String sql = "SELECT ID_Cliente, Nome, Cognome, Email, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo FROM CLIENTE WHERE ID_Cliente = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del profilo del cliente", e);
        }
        return Optional.empty();
    }

    public List<Client> getAll() {
        return findByQuery("SELECT * FROM CLIENTE ORDER BY Cognome, Nome", (Object[]) null);
    }

    public List<Client> findAll(boolean active) {
        return findByQuery("SELECT * FROM CLIENTE WHERE Cliente_Attivo = ? ORDER BY Cognome, Nome", active);
    }

    public List<Client> findAssignedToPT(int ptId) {
        String sql = """
                SELECT c.* 
                FROM CLIENTE c 
                JOIN ASSEGNA a ON c.ID_Cliente = a.ID_Cliente 
                WHERE a.ID_PT = ? AND a.Assegnazione_Attiva = 1 AND c.Cliente_Attivo = 1
                ORDER BY c.Cognome, c.Nome
                """;
        return findByQuery(sql, ptId);
    }

    private List<Client> findByQuery(String sql, Object... params) {
        List<Client> list = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore d'esecuzione della query di ricerca per Cliente", e);
        }
        return list;
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("ID_Cliente"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                rs.getString("Codice_Fiscale"),
                rs.getString("Indirizzo_Residenza"),
                rs.getDate("Data_Nascita").toLocalDate(),
                rs.getBoolean("Cliente_Attivo")
        );
    }

    /**
     * Deactivates a client and their active sheet using the stored procedure.
     */
    public void deactivate(int clientId) {
        String sql = "{CALL sp_disattiva_cliente(?)}";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, clientId);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di disattivazione tramite SP del cliente: " + clientId, e);
        }
    }

    /**
     * Reactivates a client. Note: The client will need a new sheet assigned by a PT.
     */
    public void activate(int id) {
        updateActiveStatus(id, true);
    }

    public void insert(org.DPT.users.common.dto.ClientCreationDTO data) {
        String sql = "INSERT INTO CLIENTE (Nome, Cognome, Email, Password, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.userBase().firstName());
            pstmt.setString(2, data.userBase().lastName());
            pstmt.setString(3, data.userBase().email());
            pstmt.setString(4, data.userBase().password());
            pstmt.setString(5, data.fiscalCode());
            pstmt.setString(6, data.address());
            pstmt.setDate(7, Date.valueOf(data.birthDate()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento del nuovo cliente: " + data.userBase().email(), e);
        }
    }

    public void updateActiveStatus(int clientId, boolean active) {
        String sql = "UPDATE CLIENTE SET Cliente_Attivo = ? WHERE ID_Cliente = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, clientId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di aggiornamento dello stato del cliente ad attivo", e);
        }
    }
}
