package org.DPT.login.persistence;

import org.DPT.auth.Role;
import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.login.model.LoginResult;
import org.DPT.login.model.UserCredentials;
import org.DPT.persistence.connection.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object per la gestione del Login.
 * Implementa la ricerca mirata sulla tabella corrispondente al ruolo scelto.
 */
public class LoginDAO {

    /**
     * Autentica l'utente cercando nella tabella specifica associata al ruolo.
     * @param creds Le credenziali incluse di ruolo.
     * @return LoginResult con i dati dell'utente.
     * @throws AuthException Se le credenziali sono errate o l'account è disattivato.
     */
    public LoginResult authenticate(UserCredentials creds) {
        Connection conn = DBConnectionManager.getInstance().connectAs(Role.LOGIN);
        
        String sql = getQueryForRole(creds.role());
        String idCol = getIdColumnName(creds.role());

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, creds.email());
            pstmt.setString(2, creds.password());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    checkUserStatus(rs, creds.role());

                    return new LoginResult(
                            rs.getInt(idCol),
                            creds.role(),
                            rs.getString("Nome") + " " + rs.getString("Cognome")
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
            case PROPRIETARIO -> "SELECT ID_Proprietario, Nome, Cognome FROM PROPRIETARIO WHERE Email = ? AND Password = ?";
            case PT -> "SELECT ID_PT, Nome, Cognome, PT_Attivo FROM PT WHERE Email = ? AND Password = ?";
            case SEGRETERIA -> "SELECT ID_Addetto, Nome, Cognome, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE Email = ? AND Password = ?";
            case CLIENTE -> "SELECT ID_Cliente, Nome, Cognome, Cliente_Attivo FROM CLIENTE WHERE Email = ? AND Password = ?";
            default -> throw new IllegalArgumentException("Ruolo non supportato per il login.");
        };
    }

    private String getIdColumnName(Role role) {
        return switch (role) {
            case PROPRIETARIO -> "ID_Proprietario";
            case PT -> "ID_PT";
            case SEGRETERIA -> "ID_Addetto";
            case CLIENTE -> "ID_Cliente";
            default -> "";
        };
    }

    private void checkUserStatus(ResultSet rs, Role role) throws SQLException {
        String statusCol = switch (role) {
            case PT -> "PT_Attivo";
            case SEGRETERIA -> "Addetto_Attivo";
            case CLIENTE -> "Cliente_Attivo";
            default -> null; // Il proprietario non ha colonna attivo/disattivo
        };

        if (statusCol != null && !rs.getBoolean(statusCol)) {
            throw new AuthException("L'account è stato disattivato. Contattare l'amministratore.");
        }
    }
}
