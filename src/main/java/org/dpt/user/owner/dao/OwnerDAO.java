package org.dpt.user.owner.dao;

import org.dpt.exception.DatabaseException;
import org.dpt.user.owner.model.Owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Data Access Object specifico per il Proprietario.
 */
public class OwnerDAO {
    private final Connection connection;

    private static final String FIND_BY_ID = "SELECT ID_Proprietario, Nome, Cognome, Email FROM PROPRIETARIO WHERE ID_Proprietario = ?";

    public OwnerDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Owner> findById(int id) {
        try (PreparedStatement pstmt = connection.prepareStatement(FIND_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOwner(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del profilo del proprietario", e);
        }
        return Optional.empty();
    }

    private Owner mapResultSetToOwner(ResultSet rs) throws SQLException {
        return new Owner(
                rs.getInt("ID_Proprietario"),
                rs.getString("Nome"),
                rs.getString("Cognome"),
                rs.getString("Email")
        );
    }
}
