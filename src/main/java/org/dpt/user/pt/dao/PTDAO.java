package org.dpt.user.pt.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.user.management.dto.UserCreationDTO;
import org.dpt.user.pt.model.PT;
import org.dpt.user.pt.model.PerformanceDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object specializzato per l'entità Personal Trainer.
 * -
 * Implementa le operazioni CRUD sulla tabella 'PT' e gestisce l'estrazione di 
 * reportistica avanzata sulle prestazioni degli atleti. 
 * Si avvale di query complesse con sottointerrogazioni per calcolare 
 * statistiche aggregate in tempo reale direttamente sul server MariaDB.
 */
public class PTDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_PT, Nome, Cognome, Email, PT_Attivo FROM PT WHERE ID_PT = ?";
    private static final String SELECT_ALL = "SELECT ID_PT, Nome, Cognome, Email, PT_Attivo FROM PT ORDER BY ID_PT";
    private static final String FIND_ALL_BY_STATUS = "SELECT ID_PT, Nome, Cognome, Email, PT_Attivo FROM PT WHERE PT_Attivo = ? ORDER BY ID_PT";
    private static final String INSERT_PT = "INSERT INTO PT (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_STATUS = "UPDATE PT SET PT_Attivo = ? WHERE ID_PT = ?";
    
    /** Query per report prestazioni: aggrega i dati della vista con un conteggio totale per cliente. */
    private static final String PERFORMANCE_REPORT = """
                SELECT v.Nominativo_Cliente, stats.Num_Allenamenti, v.Data, v.Durata_Minuti, v.Percentuale_Completamento
                FROM vw_prestazioni_pt v
                JOIN (
                    SELECT ID_Cliente, COUNT(*) as Num_Allenamenti
                    FROM vw_prestazioni_pt
                    WHERE ID_PT = ? AND Data BETWEEN ? AND ?
                    GROUP BY ID_Cliente
                ) stats ON v.ID_Cliente = stats.ID_Cliente
                WHERE v.ID_PT = ? AND v.Data BETWEEN ? AND ?
                ORDER BY v.Nominativo_Cliente, v.Data DESC
                """;

    public PTDAO(Connection connection) {
        this.connection = connection;
    }

    /** Carica il profilo tecnico del PT tramite ID. */
    public Optional<PT> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPT(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del profilo del PT", e);
        }
        return Optional.empty();
    }

    /** Recupera l'anagrafica completa dei Personal Trainer. */
    public List<PT> getAll() {
        return findByQuery(SELECT_ALL, null);
    }

    /** Recupera i PT filtrati per stato di attività (soft-delete). */
    public List<PT> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    private List<PT> findByQuery(String sql, Object[] params) {
        List<PT> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

    /** Registra un nuovo Personal Trainer nel sistema. */
    public void insert(UserCreationDTO data) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_PT)) {
            pstmt.setString(1, data.firstName());
            pstmt.setString(2, data.lastName());
            pstmt.setString(3, data.email());
            pstmt.setString(4, data.password());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento del nuovo PT: " + data.email(), e);
        }
    }

    /** Modifica lo stato operativo del PT (abilitazione/disabilitazione). */
    public void updateStatus(int id, boolean active) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Personal Trainer con ID " + id + " non trovato.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di aggiornamento dello stato del PT", e);
        }
    }

    /**
     * Genera un report analitico sulle prestazioni degli atleti nel periodo specificato.
     * @param ptId ID del Personal Trainer richiedente.
     * @param start Data inizio intervallo.
     * @param end Data fine intervallo.
     * @return Lista di DTO contenenti statistiche e dettagli sessioni.
     */
    public List<PerformanceDTO> getPerformanceReport(int ptId, LocalDate start, LocalDate end) {
        List<PerformanceDTO> report = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(PERFORMANCE_REPORT)) {
            pstmt.setInt(1, ptId);
            pstmt.setDate(2, java.sql.Date.valueOf(start));
            pstmt.setDate(3, java.sql.Date.valueOf(end));
            pstmt.setInt(4, ptId);
            pstmt.setDate(5, java.sql.Date.valueOf(start));
            pstmt.setDate(6, java.sql.Date.valueOf(end));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    report.add(new PerformanceDTO(
                            rs.getString("Nominativo_Cliente"),
                            rs.getInt("Num_Allenamenti"),
                            rs.getDate("Data").toLocalDate(),
                            rs.getInt("Durata_Minuti"),
                            rs.getInt("Percentuale_Completamento")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la generazione del report prestazioni", e);
        }
        return report;
    }
}
