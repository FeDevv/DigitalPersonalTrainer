package org.DPT.users.owner.dao;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.users.owner.model.Owner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OwnerDAO {
    public Optional<Owner> findById(int id) {
        String sql = "SELECT ID_Proprietario, Nome, Cognome, Email FROM PROPRIETARIO WHERE ID_Proprietario = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Owner(
                            rs.getInt("ID_Proprietario"),
                            rs.getString("Nome"),
                            rs.getString("Cognome"),
                            rs.getString("Email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il caricamento del profilo del proprietario", e);
        }
        return Optional.empty();
    }
}
