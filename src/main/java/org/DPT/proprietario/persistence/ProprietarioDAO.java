package org.DPT.proprietario.persistence;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.model.UtenteSommario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object per le operazioni amministrative del Proprietario.
 * La connessione NON viene chiusa qui per permettere il riutilizzo della sessione.
 */
public class ProprietarioDAO {

    public void insertMacchinario(int idProprietario, String nome, String descrizione) throws DatabaseException {
        String sql = "INSERT INTO MACCHINARIO (ID_Proprietario, Nome, Descrizione_Macchinario) VALUES (?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProprietario);
            pstmt.setString(2, nome);
            pstmt.setString(3, descrizione);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore inserimento macchinario: " + e.getMessage(), e);
        }
    }

    public void toggleStatoMacchinario(int id) throws DatabaseException {
        String sql = "UPDATE MACCHINARIO SET Macchinario_Attivo = NOT Macchinario_Attivo WHERE ID_Macchinario = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore cambio stato macchinario: " + e.getMessage(), e);
        }
    }

    public void insertEsercizio(int idProprietario, String nome, String descrizione, boolean corpoLibero, Integer idMacchinario) throws DatabaseException {
        String sql = "INSERT INTO ESERCIZIO (ID_Proprietario, Nome, Descrizione_Esercizio, Corpo_Libero, ID_Macchinario) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProprietario);
            pstmt.setString(2, nome);
            pstmt.setString(3, descrizione);
            pstmt.setBoolean(4, corpoLibero);
            if (idMacchinario == null) {
                pstmt.setNull(5, Types.SMALLINT);
            } else {
                pstmt.setInt(5, idMacchinario);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore inserimento esercizio: " + e.getMessage(), e);
        }
    }

    public void toggleStatoEsercizio(int id) throws DatabaseException {
        String sql = "UPDATE ESERCIZIO SET Esercizio_Attivo = NOT Esercizio_Attivo WHERE Codice_Esercizio = ?";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore cambio stato esercizio: " + e.getMessage(), e);
        }
    }

    public void insertPT(String nome, String cognome, String email, String password) throws DatabaseException {
        String sql = "INSERT INTO PT (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
        executeUserInsert(sql, nome, cognome, email, password);
    }

    public void toggleStatoPT(int id) throws DatabaseException {
        executeUpdate("UPDATE PT SET PT_Attivo = NOT PT_Attivo WHERE ID_PT = ?", id);
    }

    public void insertAddetto(String nome, String cognome, String email, String password) throws DatabaseException {
        String sql = "INSERT INTO ADDETTO_SEGRETERIA (Nome, Cognome, Email, Password) VALUES (?, ?, ?, ?)";
        executeUserInsert(sql, nome, cognome, email, password);
    }

    public void toggleStatoAddetto(int id) throws DatabaseException {
        executeUpdate("UPDATE ADDETTO_SEGRETERIA SET Addetto_Attivo = NOT Addetto_Attivo WHERE ID_Addetto = ?", id);
    }

    public void insertCliente(String nome, String cognome, String email, String password, String cf, String indirizzo, java.sql.Date dataNascita) throws DatabaseException {
        String sql = "INSERT INTO CLIENTE (Nome, Cognome, Email, Password, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, cf);
            pstmt.setString(6, indirizzo);
            pstmt.setDate(7, dataNascita);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore inserimento cliente: " + e.getMessage(), e);
        }
    }

    public void toggleStatoCliente(int id) throws DatabaseException {
        Connection conn = DBConnectionManager.getInstance().getConnection();
        boolean isAttivo;

        // 1. Verifichiamo lo stato attuale del cliente
        String checkSql = "SELECT Cliente_Attivo FROM CLIENTE WHERE ID_Cliente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    isAttivo = rs.getBoolean("Cliente_Attivo");
                } else {
                    throw new DatabaseException("Impossibile trovare il cliente con ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il controllo dello stato cliente: " + e.getMessage(), e);
        }

        // 2. Eseguiamo l'azione appropriata
        try {
            if (isAttivo) {
                // Se è attivo, lo disattiviamo tramite la Stored Procedure (che disattiva anche la scheda)
                String spSql = "{CALL sp_disattiva_cliente(?)}";
                try (CallableStatement cstmt = conn.prepareCall(spSql)) {
                    cstmt.setInt(1, id);
                    cstmt.execute();
                }
            } else {
                // Se è inattivo, lo riattiviamo semplicemente (senza riattivare vecchie schede)
                String updateSql = "UPDATE CLIENTE SET Cliente_Attivo = 1 WHERE ID_Cliente = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il cambio stato cliente: " + e.getMessage(), e);
        }
    }

    private void executeUserInsert(String sql, String nome, String cognome, String email, String password) throws DatabaseException {
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore inserimento utente: " + e.getMessage(), e);
        }
    }

    private void executeUpdate(String sql, int id) throws DatabaseException {
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Errore aggiornamento record: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // LETTURA LISTE UTENZE
    // ==========================================

    public List<UtenteSommario> getAllPT() throws DatabaseException {
        return fetchUtenti("SELECT ID_PT, Nome, Cognome, Email, PT_Attivo FROM PT", "ID_PT", "PT_Attivo");
    }

    public List<UtenteSommario> getAllAddetti() throws DatabaseException {
        return fetchUtenti("SELECT ID_Addetto, Nome, Cognome, Email, Addetto_Attivo FROM ADDETTO_SEGRETERIA", "ID_Addetto", "Addetto_Attivo");
    }

    public List<UtenteSommario> getAllClienti() throws DatabaseException {
        return fetchUtenti("SELECT ID_Cliente, Nome, Cognome, Email, Cliente_Attivo FROM CLIENTE", "ID_Cliente", "Cliente_Attivo");
    }

    private List<UtenteSommario> fetchUtenti(String sql, String idCol, String activeCol) throws DatabaseException {
        List<UtenteSommario> utenti = new ArrayList<>();
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                utenti.add(new UtenteSommario(
                        rs.getInt(idCol),
                        rs.getString("Nome"),
                        rs.getString("Cognome"),
                        rs.getString("Email"),
                        rs.getBoolean(activeCol)
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore recupero lista utenti: " + e.getMessage(), e);
        }
        return utenti;
    }
}
