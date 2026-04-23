package org.DPT.shared.catalogo.persistence;

import org.DPT.connection.DBConnectionManager;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.catalogo.model.Esercizio;
import org.DPT.shared.catalogo.model.Macchinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object condiviso per la lettura del catalogo.
 * La connessione è gestita esternamente dal DBConnectionManager.
 */
public class CatalogoDAO {

    public List<Macchinario> getAllMacchinari() throws DatabaseException {
        List<Macchinario> macchinari = new ArrayList<>();
        String sql = "SELECT ID_Macchinario, Nome, Descrizione_Macchinario, Macchinario_Attivo FROM MACCHINARIO";

        // NOTA: La Connection NON è nel try-with-resources per evitare la chiusura prematura del Singleton
        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                macchinari.add(new Macchinario(
                        rs.getInt("ID_Macchinario"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione_Macchinario"),
                        rs.getBoolean("Macchinario_Attivo")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero dei macchinari", e);
        }
        return macchinari;
    }

    public List<Esercizio> getAllEsercizi() throws DatabaseException {
        List<Esercizio> esercizi = new ArrayList<>();
        String sql = "SELECT Codice_Esercizio, Nome, Descrizione_Esercizio, Corpo_Libero, ID_Macchinario, Esercizio_Attivo FROM ESERCIZIO";

        Connection conn = DBConnectionManager.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idMacch = rs.getInt("ID_Macchinario");
                Integer idMacchObj = rs.wasNull() ? null : idMacch;

                esercizi.add(new Esercizio(
                        rs.getInt("Codice_Esercizio"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione_Esercizio"),
                        rs.getBoolean("Corpo_Libero"),
                        idMacchObj,
                        rs.getBoolean("Esercizio_Attivo")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero degli esercizi", e);
        }
        return esercizi;
    }
}
