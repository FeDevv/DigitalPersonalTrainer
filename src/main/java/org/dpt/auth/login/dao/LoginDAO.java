package org.dpt.auth.login.dao;

import org.dpt.auth.Role;
import org.dpt.exception.AuthException;
import org.dpt.exception.DatabaseException;
import org.dpt.auth.login.model.AuthToken;
import org.dpt.auth.login.model.UserCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object dedicato esclusivamente alle operazioni di autenticazione.
 * -
 * Il DAO opera sotto la connessione tecnica 'dpt_login' e ha il compito di 
 * interrogare le tabelle verticali degli attori (Proprietario, PT, ecc.) 
 * per verificare la corrispondenza delle credenziali e lo stato di attività dell'account.
 */
public class LoginDAO {
    private final Connection connection;

    /** Query atomiche parametrizzate per la verifica delle credenziali in base al ruolo. */
    private static final String AUTH_OWNER = "SELECT ID_Proprietario FROM PROPRIETARIO WHERE Email = ? AND Password = ?";
    private static final String AUTH_PT = "SELECT ID_PT, PT_Attivo FROM PT WHERE Email = ? AND Password = ?";
    private static final String AUTH_RECEPTIONIST = "SELECT ID_Addetto, Addetto_Attivo FROM ADDETTO_SEGRETERIA WHERE Email = ? AND Password = ?";
    private static final String AUTH_CLIENT = "SELECT ID_Cliente, Cliente_Attivo FROM CLIENTE WHERE Email = ? AND Password = ?";

    /**
     * Costruisce il DAO iniettando la connessione JDBC di handshake.
     * @param connection Connessione limitata al ruolo LOGIN.
     */
    public LoginDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Esegue il tentativo di autenticazione sul database.
     * -
     * Il metodo seleziona dinamicamente la query in base al ruolo richiesto e 
     * verifica se l'utenza esiste, se la password coincide e se l'account è attivo.
     * 
     * @param creds Record contenente Email, Password e Ruolo target.
     * @return AuthToken popolato con ID Utente e Ruolo in caso di successo.
     * @throws AuthException Se le credenziali sono errate o l'account è disattivato.
     * @throws DatabaseException In caso di errori SQL durante la comunicazione.
     */
    public AuthToken authenticate(UserCredentials creds) {
        String sql = getQueryForRole(creds.role());
        String idCol = getIdColumnName(creds.role());

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, creds.email());
            pstmt.setString(2, creds.password());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica dell'integrità operativa (Active Check)
                    checkUserStatus(rs, creds.role());

                    return new AuthToken(
                            rs.getInt(idCol),
                            creds.role()
                    );
                } else {
                    throw new AuthException("Autenticazione fallita: email o password non riconosciute per il profilo " + creds.role().getSingular() + ".");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore tecnico durante la procedura di handshake", e);
        }
    }

    /** Mappa il ruolo alla query SQL corrispondente. */
    private String getQueryForRole(Role role) {
        return switch (role) {
            case OWNER -> AUTH_OWNER;
            case PT -> AUTH_PT;
            case RECEPTIONIST -> AUTH_RECEPTIONIST;
            case CLIENT -> AUTH_CLIENT;
            default -> throw new IllegalArgumentException("Sicurezza: il ruolo specificato non supporta l'autenticazione diretta.");
        };
    }

    /** Risolve il nome della colonna PK in base alla tabella del ruolo. */
    private String getIdColumnName(Role role) {
        return switch (role) {
            case OWNER -> "ID_Proprietario";
            case PT -> "ID_PT";
            case RECEPTIONIST -> "ID_Addetto";
            case CLIENT -> "ID_Cliente";
            default -> throw new IllegalStateException("Configurazione mancante per la colonna ID del ruolo: " + role);
        };
    }

    /**
     * Verifica se l'account dell'utente è abilitato all'accesso.
     * @throws AuthException Se l'utente è stato marcato come disattivato nel database.
     */
    private void checkUserStatus(ResultSet rs, Role role) throws SQLException {
        String statusCol = switch (role) {
            case PT -> "PT_Attivo";
            case RECEPTIONIST -> "Addetto_Attivo";
            case CLIENT -> "Cliente_Attivo";
            default -> null; // Il Proprietario è sempre attivo per definizione di schema
        };

        if (statusCol != null && !rs.getBoolean(statusCol)) {
            throw new AuthException("Accesso negato: l'account selezionato risulta disattivato dal sistema centrale.");
        }
    }
}
