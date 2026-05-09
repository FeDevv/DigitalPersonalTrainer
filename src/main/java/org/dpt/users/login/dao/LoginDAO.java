package org.dpt.users.login.dao;

import org.dpt.shared.auth.Role;
import org.dpt.exception.AuthException;
import org.dpt.exception.DatabaseException;
import org.dpt.users.login.model.AuthToken;
import org.dpt.users.login.model.UserCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object per la gestione del Login.
 */
public class LoginDAO {
    private final Connection connection;

    private static final String AUTH_OWNER = "SELECT ID_Proprietario FROM PROPRIETARIO WHERE Email = ? AND Password = ?";
    private static final String AUTH_PT = "SELECT ID_PT, PT_Attivo FROM PT WHERE Email = ? AND Password = ?";
    private static final String AUTH_RECEPTIONIST = "SELECT ID_Addetto, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE Email = ? AND Password = ?";
    private static final String AUTH_CLIENT = "SELECT ID_Cliente, Cliente_Attivo FROM CLIENTE WHERE Email = ? AND Password = ?";

    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    public AuthToken authenticate(UserCredentials creds) {
        String sql = getQueryForRole(creds.role());
        String idCol = getIdColumnName(creds.role());

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, creds.email());
            pstmt.setString(2, creds.password());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    checkUserStatus(rs, creds.role());

                    return new AuthToken(
                            rs.getInt(idCol),
                            creds.role()
                    );
                } else {
                    throw new AuthException("Email o Password errati per il tipo di utenza selezionato.");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore tecnico durante l'autenticazione", e);
        }
    }

    private String getQueryForRole(Role role) {
        return switch (role) {
            case OWNER -> AUTH_OWNER;
            case PT -> AUTH_PT;
            case RECEPTIONIST -> AUTH_RECEPTIONIST;
            case CLIENT -> AUTH_CLIENT;
            default -> throw new IllegalArgumentException("Ruolo non supportato per il login.");
        };
    }

    private String getIdColumnName(Role role) {
        return switch (role) {
            case OWNER -> "ID_Proprietario";
            case PT -> "ID_PT";
            case RECEPTIONIST -> "ID_Addetto";
            case CLIENT -> "ID_Cliente";
            default -> throw new IllegalStateException("ID colonna non definito per il ruolo: " + role);
        };
    }

    private void checkUserStatus(ResultSet rs, Role role) throws SQLException {
        String statusCol = switch (role) {
            case PT -> "PT_Attivo";
            case RECEPTIONIST -> "Addetto_Attivo";
            case CLIENT -> "Cliente_Attivo";
            default -> null;
        };

        if (statusCol != null && !rs.getBoolean(statusCol)) {
            throw new AuthException("L'account è stato disattivato. Contattare la segreteria.");
        }
    }
}
