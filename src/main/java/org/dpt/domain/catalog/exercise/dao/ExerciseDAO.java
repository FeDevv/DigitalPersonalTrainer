package org.dpt.domain.catalog.exercise.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.exception.EntityNotFoundException;
import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.exercise.dto.ExerciseCreationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) specializzato per l'entità Esercizio.
 * -
 * Implementa le operazioni CRUD e di ricerca sulla tabella 'ESERCIZIO'.
 * Rispetta il principio di "Verticalizzazione e Purezza": incapsula esclusivamente 
 * la logica JDBC relativa agli esercizi, senza istanziare altri DAO, delegando 
 * il coordinamento tra entità diverse ai LogicController.
 */
public class ExerciseDAO {
    private final Connection connection;

    /** Query SQL predefinite per l'accesso ai dati. */
    private static final String FIND_BY_ID = "SELECT Codice_Esercizio, ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo FROM ESERCIZIO WHERE Codice_Esercizio = ?";
    private static final String SELECT_ALL = "SELECT Codice_Esercizio, ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo FROM ESERCIZIO ORDER BY Codice_Esercizio";
    private static final String FIND_ALL_BY_STATUS = "SELECT Codice_Esercizio, ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo FROM ESERCIZIO WHERE Esercizio_Attivo = ? ORDER BY Codice_Esercizio";
    private static final String SELECT_SELECTABLE = "SELECT * FROM vw_esercizi_selezionabili";
    private static final String INSERT_EXERCISE = "INSERT INTO ESERCIZIO (ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo) VALUES (?, ?, ?, ?, ?, 1)";
    private static final String UPDATE_STATUS = "UPDATE ESERCIZIO SET Esercizio_Attivo = ? WHERE Codice_Esercizio = ?";

    /**
     * Inizializza il DAO con una connessione JDBC attiva.
     * @param connection Connessione fornita dal DBConnectionManager.
     */
    public ExerciseDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Recupera un esercizio specifico tramite il suo identificativo univoco.
     * @param id Codice dell'esercizio.
     * @return Optional contenente l'Exercise se trovato, vuoto altrimenti.
     */
    public Optional<Exercise> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExercise(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento dell'esercizio tramite ID: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Recupera l'elenco completo di tutti gli esercizi (attivi e non).
     * @return Lista di oggetti Exercise.
     */
    public List<Exercise> getAll() {
        return findByQuery(SELECT_ALL, null);
    }

    /**
     * Filtra gli esercizi in base allo stato di attività.
     * Non usato per rispettare il vincolo di least knowledge (un pt non dovrebbe poter vedere esercizi
     * che non sono praticabili a causa del macchinario guasto.)
     * @param active true per visualizzare solo quelli operativi.
     * @return Lista filtrata di Exercise.
     */
    public List<Exercise> findAll(boolean active) {
        return findByQuery(FIND_ALL_BY_STATUS, new Object[]{active});
    }

    /**
     * Recupera solo gli esercizi effettivamente selezionabili per una scheda.
     * Un esercizio è selezionabile se è attivo E (è a corpo libero O il macchinario associato è attivo).
     * @return Lista di esercizi disponibili filtrati tramite vista DB.
     */
    public List<Exercise> findAllSelectable() {
        return findByQuery(SELECT_SELECTABLE, null);
    }

    /**
     * Metodo helper privato per l'esecuzione di query di selezione generiche.
     */
    private List<Exercise> findByQuery(String sql, Object[] params) {
        List<Exercise> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToExercise(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'esecuzione della query di ricerca per esercizio", e);
        }
        return list;
    }

    /**
     * Inserisce un nuovo esercizio nel database.
     * 
     * @param data DTO contenente i dati di creazione validati.
     * @param ownerId ID del proprietario che esegue l'operazione.
     * @return L'oggetto Exercise persistito, comprensivo di ID generato.
     * @throws DatabaseException In caso di violazione dei vincoli (es. trigger corpo libero).
     */
    public Exercise insert(ExerciseCreationDTO data, int ownerId) {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_EXERCISE, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ownerId);
            if (data.machineId() != null) {
                pstmt.setInt(2, data.machineId());
            } else {
                pstmt.setNull(2, Types.SMALLINT);
            }
            pstmt.setString(3, data.name());
            pstmt.setString(4, data.description());
            pstmt.setBoolean(5, data.isBodyweight());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Exercise(
                            generatedKeys.getInt(1),
                            ownerId,
                            data.machineId(),
                            data.name(),
                            data.description(),
                            data.isBodyweight(),
                            true
                    );
                } else {
                    throw new DatabaseException("Creazione dell'esercizio non riuscita: no ID generated.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inserimento nel database dell'esercizio " + data.name(), e);
        }
    }

    /**
     * Modifica lo stato di attività di un esercizio (attivazione/disattivazione).
     * @param id ID dell'esercizio.
     * @param active Nuovo stato.
     * @throws EntityNotFoundException Se l'ID non corrisponde a nessun esercizio esistente.
     */
    public void updateStatus(int id, boolean active) {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new EntityNotFoundException("Esercizio con ID " + id + " non trovato.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento dello stato dell'esercizio: " + id, e);
        }
    }

    /**
     * Mappa una riga del ResultSet nell'oggetto di dominio Exercise.
     * Centralizza la logica di estrazione per garantire coerenza tra le diverse query.
     */
    private Exercise mapResultSetToExercise(ResultSet rs) throws SQLException {
        int mId = rs.getInt("ID_Macchinario");
        Integer machineId = rs.wasNull() ? null : mId;

        return new Exercise(
                rs.getInt("Codice_Esercizio"),
                rs.getInt("ID_Proprietario"),
                machineId,
                rs.getString("Nome"),
                rs.getString("Descrizione_Esercizio"),
                rs.getBoolean("Corpo_Libero"),
                rs.getBoolean("Esercizio_Attivo")
        );
    }
}
