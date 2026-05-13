package org.dpt.connection;

import org.dpt.auth.Role;
import org.dpt.exception.DatabaseException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestore centralizzato del ciclo di vita delle connessioni JDBC verso MariaDB.
 * -
 * Implementa il pattern Singleton tramite l'idioma "Bill Pugh Holder" per garantire
 * la massima efficienza in termini di thread-safety e caricamento lazy, evitando
 * l'overhead della sincronizzazione esplicita.
 * -
 * Il componente agisce come motore del meccanismo di sicurezza RBAC (Role-Based Access Control)
 * dell'applicazione. Implementa la strategia "Handshake & Switch": dopo l'autenticazione
 * iniziale, gestisce il ricollegamento fisico al database utilizzando l'utente DBMS
 * specifico per il ruolo dell'utente, garantendo così l'integrità dei dati direttamente 
 * tramite i privilegi definiti sul server SQL.
 */
public class DBConnectionManager {

    private final Properties dbProps;
    private Connection currentConnection;

    /**
     * Costruttore privato. Carica le configurazioni di rete dal file db.properties.
     * @throws DatabaseException Se il file di configurazione è mancante o illeggibile.
     */
    private DBConnectionManager() {
        this.dbProps = new Properties();
        loadProperties();
    }

    /**
     * Holder statico per l'istanza Singleton. 
     * Caricato dalla JVM solo alla prima invocazione di getInstance().
     */
    private static class InstanceHolder {
        private static final DBConnectionManager INSTANCE = new DBConnectionManager();
    }

    /**
     * Restituisce il punto di accesso unico al gestore delle connessioni.
     * @return L'istanza Singleton di DBConnectionManager.
     */
    public static DBConnectionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Carica le proprietà di configurazione dal classpath.
     * In caso di errore, viene sollevata una DatabaseException per segnalare 
     * l'impossibilità di inizializzare lo strato di persistenza.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("Configurazione db.properties non trovata nel classpath.");
            }
            dbProps.load(input);
        } catch (IOException ex) {
            throw new DatabaseException("Errore critico nel caricamento delle proprietà del DB", ex);
        }
    }

    /**
     * Esegue lo switching della connessione in base al ruolo specificato.
     * -
     * Questo metodo chiude la connessione esistente e ne instaura una nuova 
     * utilizzando le credenziali MariaDB associate al ruolo. Questo approccio 
     * delega la sicurezza dei dati al DBMS, sfruttando i GRANT specifici.
     * 
     * @param role Il ruolo (Role) con cui stabilire la sessione database.
     * @return La connessione JDBC attiva con i privilegi del ruolo.
     * @throws DatabaseException Se la connessione fallisce o mancano le credenziali.
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

    /**
     * Recupera il riferimento alla connessione attualmente attiva.
     * @return Connection JDBC corrente, o null se non è stata stabilita una sessione.
     */
    public Connection getConnection() {
        return currentConnection;
    }

    /**
     * Chiude la sessione database corrente rilasciando le risorse sul server.
     * Garantisce l'idempotenza e gestisce eventuali eccezioni durante il teardown.
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
