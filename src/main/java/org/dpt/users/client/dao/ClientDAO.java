package org.dpt.users.client.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.users.client.model.Client;
import org.dpt.users.common.dto.ClientCreationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_Cliente, Nome, Cognome, Email, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo FROM CLIENTE WHERE ID_Cliente = ?";
    private static final String SELECT_ALL = "SELECT * FROM CLIENTE ORDER BY ID_Cliente";
    private static final String FIND_ALL_BY_STATUS = "SELECT * FROM CLIENTE WHERE Cliente_Attivo = ? ORDER BY ID_Cliente";
    private static final String FIND_ASSIGNED_TO_PT = """
                SELECT c.* 
                FROM CLIENTE c 
                JOIN ASSEGNA a ON c.ID_Cliente = a.ID_Cliente 
                WHERE a.ID_PT = ? AND a.Assegnazione_Attiva = 1 AND c.Cliente_Attivo = 1
                ORDER BY c.ID_Cliente
                """;
    private static final String DEACTIVATE_CLIENT = "{CALL sp_disattiva_cliente(?)}";
    private static final String INSERT_CLIENT = "INSERT INTO CLIENTE (Nome, Cognome, Email, Password, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE CLIENTE SET Cliente_Attivo = ? WHERE ID_Cliente = ?";

    public ClientDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Client> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
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
        return findByQuery(SELECT_ALL, null);
    }

    public List<Client> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    public List<Client> findAssignedToPT(int ptId) {
        return findByQuery(FIND_ASSIGNED_TO_PT, new Object[]{ptId});
    }

    private List<Client> findByQuery(String sql, Object[] params) {
        List<Client> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

    public void deactivate(int clientId) {
        try (CallableStatement cstmt = connection.prepareCall(DEACTIVATE_CLIENT)) {
            cstmt.setInt(1, clientId);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di disattivazione tramite SP del cliente: " + clientId, e);
        }
    }

    public void activate(int id) {
        updateActiveStatus(id, true);
    }

    public void insert(ClientCreationDTO data) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_CLIENT)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, clientId);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Cliente con ID " + clientId + " non trovato.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di aggiornamento dello stato del cliente", e);
        }
    }
}
