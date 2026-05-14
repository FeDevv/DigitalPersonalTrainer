package org.dpt.domain.workout.session.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.domain.workout.session.model.WorkoutSession;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per la gestione operativa delle sessioni di allenamento.
 * -
 * Gestisce l'apertura e la chiusura dei workout e il recupero della cronologia 
 * delle sessioni per scheda. Si integra con la logica DB: all'apertura della 
 * sessione, un trigger ('trg_popola_serie_sessione') genera automaticamente 
 * tutte le righe dei set previsti nella tabella SERIE_ESEGUITA.
 */
public class WorkoutSessionDAO {
    private final Connection connection;

    /** Query SQL per la gestione delle sessioni. */
    private static final String START_SESSION = "INSERT INTO SESSIONE (ID_Scheda, Data, Ora_Inizio, Percentuale_Completamento) VALUES (?, ?, ?, 0)";
    private static final String END_SESSION = "UPDATE SESSIONE SET Ora_Fine = ? WHERE ID_Sessione = ?";
    private static final String FIND_ALL_BY_SHEET_ID = "SELECT ID_Sessione, ID_Scheda, Data, Ora_Inizio, Ora_Fine, Percentuale_Completamento FROM SESSIONE WHERE ID_Scheda = ? ORDER BY Data DESC, Ora_Inizio DESC";

    public WorkoutSessionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Avvia una nuova sessione di allenamento persistendola nel DB.
     * @param sheetId La scheda attiva da utilizzare come base.
     * @return L'oggetto WorkoutSession creato con ID generato.
     */
    public WorkoutSession startSession(int sheetId) {
        LocalDate now = LocalDate.now();
        LocalTime startTime = LocalTime.now();

        try (PreparedStatement pstmt = connection.prepareStatement(START_SESSION, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sheetId);
            pstmt.setDate(2, Date.valueOf(now));
            pstmt.setTime(3, Time.valueOf(startTime));

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new WorkoutSession(
                            generatedKeys.getInt(1),
                            sheetId,
                            now,
                            startTime,
                            null,
                            0
                    );
                } else {
                    throw new DatabaseException("Integrità Sessione: Impossibile generare l'ID per la nuova sessione.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di avvio della sessione per la scheda: " + sheetId, e);
        }
    }

    /**
     * Marca la chiusura temporale di una sessione attiva.
     * @param sessionId ID della sessione da terminare.
     */
    public void endSession(int sessionId) {
        try (PreparedStatement pstmt = connection.prepareStatement(END_SESSION)) {
            pstmt.setTime(1, Time.valueOf(LocalTime.now()));
            pstmt.setInt(2, sessionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la chiusura della sessione: " + sessionId, e);
        }
    }

    /**
     * Recupera l'elenco cronologico di tutte le sessioni svolte con una specifica scheda.
     */
    public List<WorkoutSession> findAllBySheetId(int sheetId) {
        List<WorkoutSession> sessions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_ALL_BY_SHEET_ID)) {
            pstmt.setInt(1, sheetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore di recupero della cronologia per la scheda: " + sheetId, e);
        }
        return sessions;
    }

    /** Mappa una riga della tabella SESSIONE nel modello WorkoutSession. */
    private WorkoutSession mapResultSetToSession(ResultSet rs) throws SQLException {
        Time endTime = rs.getTime("Ora_Fine");
        return new WorkoutSession(
                rs.getInt("ID_Sessione"),
                rs.getInt("ID_Scheda"),
                rs.getDate("Data").toLocalDate(),
                rs.getTime("Ora_Inizio").toLocalTime(),
                endTime != null ? endTime.toLocalTime() : null,
                rs.getInt("Percentuale_Completamento")
        );
    }
}
