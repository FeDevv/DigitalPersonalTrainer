package org.dpt.user.receptionist.dao;

import org.dpt.exception.DatabaseException;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Data Access Object specializzato per la gestione dei legami contrattuali tra Istruttori e Atleti.
 * -
 * Interagisce con la tabella di relazione 'ASSEGNA'. A differenza dei DAO standard,
 * questo componente delega l'intera logica di business alla Stored Procedure 
 * 'sp_assegna_pt' lato database, che garantisce l'atomicità dell'operazione 
 * e il rispetto dei vincoli di integrità referenziale definiti nello schema.
 */
public class AssignmentDAO {
    private final Connection connection;

    private static final String CREATE_ASSIGNMENT = "{CALL sp_assegna_pt(?, ?, ?)}";

    public AssignmentDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Instaura una nuova relazione professionale tra un Personal Trainer e un Cliente.
     * 
     * @param ptId ID dell'istruttore.
     * @param clientId ID del cliente.
     * @param receptionistId ID dell'operatore che registra l'assegnazione.
     * @throws DatabaseException In caso di violazione dei vincoli o errori procedurali SQL.
     */
    public void createAssignment(int ptId, int clientId, int receptionistId) {
        try (CallableStatement cstmt = connection.prepareCall(CREATE_ASSIGNMENT)) {
            cstmt.setInt(1, ptId);
            cstmt.setInt(2, clientId);
            cstmt.setInt(3, receptionistId);
            cstmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Errore tecnico durante l'assegnazione.", e);
        }
    }
}
