package org.dpt.user.client.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.user.client.model.Client;
import org.dpt.user.client.model.ClientPersonalInfo;
import org.dpt.user.management.dto.ClientCreationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object specializzato per la gestione dell'entità Cliente.
 * -
 * Implementa l'accesso ai dati per la tabella 'CLIENTE', gestendo sia le query 
 * di ricerca semplici che le operazioni amministrative complesse. 
 * Si interfaccia con:
 * <ul>
 *   <li><b>Stored Procedures:</b> Utilizza {@code sp_disattiva_cliente} per garantire 
 *       una disattivazione atomica (soft-delete) di account e schede associate.</li>
 *   <li><b>Join Complesse:</b> Gestisce il recupero dei clienti assegnati a uno specifico 
 *       Personal Trainer tramite l'analisi della tabella di relazione 'ASSEGNA'.</li>
 * </ul>
 */
public class ClientDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_Cliente, Nome, Cognome, Email, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo FROM CLIENTE WHERE ID_Cliente = ?";
    private static final String SELECT_ALL = "SELECT ID_Cliente, Nome, Cognome, Email, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo FROM CLIENTE ORDER BY ID_Cliente";
    private static final String FIND_ALL_BY_STATUS = "SELECT ID_Cliente, Nome, Cognome, Email, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo FROM CLIENTE WHERE Cliente_Attivo = ? ORDER BY ID_Cliente";
    
    /** Query di ricerca per PT: estrae solo gli atleti attivi attualmente assegnati all'istruttore. */
    private static final String FIND_ASSIGNED_TO_PT = """
                SELECT c.ID_Cliente, c.Nome, c.Cognome, c.Email, c.Codice_Fiscale, c.Indirizzo_Residenza, c.Data_Nascita, c.Cliente_Attivo 
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

    /** Recupera il profilo completo di un cliente tramite il suo identificativo univoco. */
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

    /** Restituisce l'elenco di tutti i clienti registrati nel sistema. */
    public List<Client> getAll() {
        return findByQuery(SELECT_ALL, null);
    }

    /** Filtra l'anagrafica clienti in base allo stato di attivazione. */
    public List<Client> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    /** Recupera la lista degli atleti seguiti da un determinato Personal Trainer. */
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

    /** Centralizza il mapping tra le righe JDBC e l'oggetto di dominio Client. */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        ClientPersonalInfo info = new ClientPersonalInfo(
                rs.getString("Codice_Fiscale"),
                rs.getString("Indirizzo_Residenza"),
                rs.getDate("Data_Nascita").toLocalDate()
        );

        return new Client(
                rs.getInt("ID_Cliente"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email"),
                info,
                rs.getBoolean("Cliente_Attivo")
        );
    }

    /** 
     * Esegue la disattivazione logica del cliente invocando la logica procedurale del database.
     * Questa operazione è irreversibile a livello di schede attive.
     */
    public void deactivate(int clientId) {
        try (CallableStatement cstmt = connection.prepareCall(DEACTIVATE_CLIENT)) {
            cstmt.setInt(1, clientId);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore di disattivazione tramite SP del cliente: " + clientId, e);
        }
    }

    /** Riattiva un profilo cliente precedentemente disabilitato. */
    public void activate(int id) {
        updateActiveStatus(id, true);
    }

    /** Registra una nuova anagrafica cliente nel database. */
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

    /** Metodo atomico per la modifica dello stato di attività di un cliente. */
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
