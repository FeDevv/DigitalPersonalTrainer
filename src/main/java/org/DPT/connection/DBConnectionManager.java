package org.DPT.connection;

import org.DPT.shared.auth.Role;
import org.DPT.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestore delle connessioni al Database (Singleton Bill Pugh).
 * Ottimizzato per thread-safety e performance senza blocchi synchronized.
 * Segue il principio di separazione delle responsabilità: nessuna interazione diretta con l'UI.
 */
public class DBConnectionManager {

    private final Properties dbProps;
    private Connection currentConnection;

    /**
     * Costruttore privato per impedire l'istanziazione esterna.
     */
    private DBConnectionManager() {
        this.dbProps = new Properties();
        loadProperties();
    }

    /**
     * Classe interna statica (Bill Pugh Holder).
     * Caricata dalla JVM solo alla prima chiamata di getInstance().
     */
    private static class InstanceHolder {
        private static final DBConnectionManager INSTANCE = new DBConnectionManager();
    }

    public static DBConnectionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("Configurazione db.properties non trovata nel classpath.");
            }
            dbProps.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Errore critico nel caricamento delle proprietà del DB", ex);
        }
    }

    /**
     * Implementa il Role Switching chiudendo la connessione attiva e aprendone una nuova.
     * @param role Il ruolo con cui collegarsi.
     * @return La nuova connessione stabilita.
     * @throws DatabaseException Se la connessione fallisce o le credenziali mancano.
     */
    public Connection connectAs(Role role) throws DatabaseException {
        closeConnection();

        String userKey = role.getPropertyKey() + ".user";
        String passKey = role.getPropertyKey() + ".password";
        String url = dbProps.getProperty("db.url");

        String user = dbProps.getProperty(userKey);
        String pass = dbProps.getProperty(passKey);

        if (user == null || pass == null) {
            throw new DatabaseException("Credenziali mancanti nel file di configurazione per il ruolo: " + role);
        }

        try {
            currentConnection = DriverManager.getConnection(url, user, pass);
            return currentConnection;
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile stabilire una connessione come " + role, e);
        }
    }

    public Connection getConnection() {
        return currentConnection;
    }

    /**
     * Chiude la connessione corrente se aperta.
     * Gestisce l'eventuale SQLException rilanciandola come DatabaseException 
     * per permettere ai layer superiori di decidere come notificare l'errore.
     */
    public void closeConnection() {
        try {
            if (currentConnection != null && !currentConnection.isClosed()) {
                currentConnection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la chiusura della connessione al database", e);
        } finally {
            currentConnection = null;
        }
    }
}
