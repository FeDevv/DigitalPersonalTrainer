package org.dpt.user.receptionist.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.user.receptionist.model.Receptionist;
import org.dpt.user.management.dto.UserCreationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object specializzato per l'entità Addetto Segreteria.
 * -
 * Implementa le operazioni CRUD sulla tabella 'ADDETTO_SEGRETERIA'. 
 * Gestisce il ciclo di vita delle utenze amministrative di supporto, 
 * includendo la registrazione iniziale e il management dello stato operativo.
 */
public class ReceptionistDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_Addetto, Nome, Cognome, Email, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE ID_Addetto = ?";
    private static final String SELECT_ALL = "SELECT ID_Addetto, Nome, Cognome, Email, Addetto_Attivo FROM ADDETTO_SEGRETERIA ORDER BY ID_Addetto";
    private static final String FIND_ALL_BY_STATUS = "SELECT ID_Addetto, Nome, Cognome, Email, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE Addetto_Attivo = ? ORDER BY ID_Addetto";
    private static final String INSERT_RECEPTIONIST = "INSERT INTO ADDETTO_SEGRETERIA (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE ADDETTO_SEGRETERIA SET Addetto_Attivo = ? WHERE ID_Addetto = ?";

    public ReceptionistDAO(Connection connection) {
        this.connection = connection;
    }

    /** Recupera il profilo dell'addetto tramite identificativo univoco. */
    public Optional<Receptionist> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
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

    /** Restituisce l'elenco completo di tutto il personale di segreteria. */
    public List<Receptionist> getAll() {
        return findByQuery(SELECT_ALL, null);
    }

    /** Recupera gli addetti filtrati per stato di attività (soft-delete). */
    public List<Receptionist> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    private List<Receptionist> findByQuery(String sql, Object[] params) {
        List<Receptionist> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

    /** Registra un nuovo operatore di segreteria nel database. */
    public void insert(UserCreationDTO data) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_RECEPTIONIST)) {
            pstmt.setString(1, data.firstName());
            pstmt.setString(2, data.lastName());
            pstmt.setString(3, data.email());
            pstmt.setString(4, data.password());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento del nuovo addetto: " + data.email(), e);
        }
    }

    /** Modifica lo stato operativo (abilitazione/disabilitazione) dell'account. */
    public void updateStatus(int id, boolean active) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Addetto Segreteria con ID " + id + " non trovato.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore d'aggiornamento dello stato dell'ADDETTO SEGRETERIA", e);
        }
    }
}
