# Digital Personal Trainer - Technical Specification & Engineering Manifesto

## 📖 Panoramica del Progetto
**Digital Personal Trainer (DPT)** è un sistema gestionale per palestre progettato con un'architettura **Thin-Client**. Il cuore della logica di business risiede nel database MariaDB (Trigger, Stored Procedure, Viste), mentre l'applicazione Java funge da interfaccia intelligente, sicura e performante.

---

## 🎯 Obiettivi Funzionali
1.  **Controllo d'Accesso Granulare**: Gestione di 4 attori (Proprietario, PT, Segreteria, Cliente) con permessi distinti.
2.  **Automazione Workout**: Tracking delle sessioni in tempo reale con calcolo automatico del completamento via DB.
3.  **Integrità del Dato**: Garanzia di coerenza tramite vincoli di dominio e asserzioni procedurali lato server.

---

## 🏗 Architettura del Software

### 1. Pattern Architetturale: MVC (Model-View-Controller)
Il sistema è rigorosamente diviso per separare le responsabilità:
*   **Model**: 
    *   **JavaBeans/DTO**: Classi che incapsulano i dati (es. `ClienteBean`) per lo scambio tra i layer.
    *   **Java Records**: Utilizzati per oggetti di puro trasporto dati immutabili, sfruttando le feature di Java 25.
    *   **DAO**: Logica di accesso JDBC.
*   **View**: Interfaccia CLI interattiva basata su console (`System.out`/`Scanner`).
*   **Controller**: Orchestrazione del flusso, validazione dell'input e gestione delle eccezioni tra View e Model.

### 2. Gestione degli Errori: Gerarchia di Eccezioni Custom (Unchecked)
Il sistema utilizza una gerarchia di **Unchecked Exceptions** per ridurre il boilerplate e favorire una Clean Architecture:
*   `DPTException` (Base astratta, estende `RuntimeException`)
    *   `DatabaseException`: Errori di persistenza o connessione.
    *   `AuthException`: Errori di autenticazione (Login fallito).
    *   `ValidationException`: Input utente non conforme ai requisiti.

### 3. Sicurezza: Architettura "Handshake & Switch" (RBAC)
La sicurezza è delegata al Database MariaDB tramite il meccanismo di Role-Based Access Control:
1.  **Fase di Handshake**: Accesso iniziale con l'utente `dpt_login` (permessi minimi, sola lettura credenziali).
2.  **Fase di Switching**: Dopo l'autenticazione, l'app ricollega la sessione usando l'utente MariaDB specifico per il ruolo (Proprietario, PT, Segreteria, Cliente).
3.  **Enforcement**: I permessi (GRANT/REVOKE) sono applicati a livello fisico dal motore del DB.

### 4. Design Patterns & Tecnologie
*   **Singleton (Bill Pugh)**: `DBConnectionManager` per una gestione efficiente e thread-safe della connessione.
*   **Java Records**: Utilizzati per DTO e oggetti di trasporto dati immutabili (es. `LoginResult`), ottimizzando l'uso della memoria (8GB RAM).
*   **DAO**: Astrazione dell'accesso ai dati per isolare il codice SQL.
*   **Dual-Controller MVC**: Separazione tra Logic Controller (regole di business) e Interface Controller (CLI/GUI).

---

## 🛠 Roadmap Operativa
1. [ ] Configurazione `pom.xml` (MariaDB Driver & JUnit 5).
2. [ ] Implementazione `DBConnectionManager` (Singleton).
3. [ ] Creazione della gerarchia delle Eccezioni Custom.
4. [ ] Definizione dei Modelli (POJO) basati sullo schema SQL.
5. [ ] Sviluppo dei primi DAO e test di connessione.
