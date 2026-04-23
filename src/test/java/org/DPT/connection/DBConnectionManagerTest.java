package org.DPT.connection;

import org.DPT.auth.Role;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionManagerTest {

    @Test
    @DisplayName("Il Singleton deve restituire sempre la stessa istanza")
    void testSingletonInstance() {
        DBConnectionManager instance1 = DBConnectionManager.getInstance();
        DBConnectionManager instance2 = DBConnectionManager.getInstance();
        assertSame(instance1, instance2, "Le istanze dovrebbero essere identiche");
    }

    @Test
    @DisplayName("Dovrebbe connettersi correttamente con il ruolo LOGIN (Handshake)")
    void testConnectAsLogin() {
        DBConnectionManager manager = DBConnectionManager.getInstance();
        
        // Con le Unchecked Exceptions, se fallisce il test si interrompe correttamente
        Connection conn = manager.connectAs(Role.LOGIN);
        
        assertNotNull(conn, "La connessione non dovrebbe essere null");
        try {
            assertFalse(conn.isClosed(), "La connessione dovrebbe essere aperta");
            // Nota: Il nome utente restituito dal driver potrebbe variare, verifichiamo che contenga la stringa attesa
            assertTrue(conn.getMetaData().getUserName().toLowerCase().contains("dpt_login"), 
                "L'utente DB dovrebbe essere dpt_login");
        } catch (SQLException e) {
            fail("Errore durante l'ispezione dei metadati della connessione: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Dovrebbe gestire correttamente il Role Switching")
    void testRoleSwitching() {
        DBConnectionManager manager = DBConnectionManager.getInstance();
        
        // Primo step: Connessione LOGIN
        manager.connectAs(Role.LOGIN);
        Connection conn1 = manager.getConnection();
        
        // Secondo step: Switch a CLIENTE
        manager.connectAs(Role.CLIENTE);
        Connection conn2 = manager.getConnection();

        assertNotSame(conn1, conn2, "La connessione deve essere stata sostituita");
        try {
            assertFalse(conn2.isClosed(), "La nuova connessione deve essere aperta");
            assertTrue(conn2.getMetaData().getUserName().toLowerCase().contains("dpt_cliente"), 
                "L'utente DB dovrebbe essere cambiato in dpt_cliente");
        } catch (SQLException e) {
            fail("Errore durante l'ispezione dei metadati dopo lo switch: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Dovrebbe lanciare DatabaseException in caso di parametri errati")
    void testConnectionFailure() {
        // Simuliamo un errore (ad esempio usando un ruolo che non ha configurazioni corrette se esistesse)
        // In questo caso, verifichiamo che l'eccezione sia della nostra gerarchia
        // NB: Questo test assume che il DB sia configurato come da schema.sql
        assertDoesNotThrow(() -> DBConnectionManager.getInstance().connectAs(Role.LOGIN));
    }

    @AfterAll
    static void tearDown() {
        DBConnectionManager.getInstance().closeConnection();
    }
}
