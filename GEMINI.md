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
    *   **JavaBeans/DTO**: Classi che incapsulano i dati per lo scambio tra i layer.
    *   **Java Records**: Utilizzati per oggetti di puro trasporto dati immutabili (Java 25).
    *   **DAO**: Logica di accesso JDBC granulare.
*   **View**: Interfaccia CLI interattiva (System.out/Scanner) o GUI futura.
*   **Controller**: Diviso in **Logic Controller** (regole di business/flusso) e **Interface Controller** (gestione I/O specifica dell'interfaccia).

### 2. Orchestration & Global Lifecycle
L'applicazione è governata da un **Orchestrator** centrale che gestisce:
*   **Resource Sharing**: Gestione di un unico stream `Scanner` iniettato nei moduli via Dependency Injection.
*   **Bootstrapping**: Configurazione iniziale del sistema (CLI vs GUI) tramite il modulo `boot`.
*   **Routing**: Instradamento dell'utente verso il modulo specifico basato sul Ruolo ottenuto post-login.
*   **Teardown**: Chiusura sicura di connessioni DB e stream di sistema.

### 3. Sicurezza: Architettura "Handshake & Switch" (RBAC)
La sicurezza è delegata al Database MariaDB tramite il meccanismo di Role-Based Access Control:
1.  **Fase di Handshake**: Accesso iniziale con l'utente `dpt_login` (permessi minimi, sola lettura credenziali).
2.  **Fase di Switching**: Dopo l'autenticazione, l'app ricollega la sessione usando l'utente MariaDB specifico per il ruolo (Proprietario, PT, Segreteria, Cliente).
3.  **Enforcement**: I permessi (GRANT/REVOKE) sono applicati a livello fisico dal motore del DB.

### 4. Design Patterns & Tecnologie
*   **Singleton (Bill Pugh)**: `DBConnectionManager` per una gestione efficiente e thread-safe della connessione.
*   **Dual-Controller**: Ogni modulo (Boot, Login, PT) separa la logica decisionale dall'acquisizione dell'input, garantendo che il punto d'ingresso sia sempre il controller logico.
*   **Dependency Injection**: Passaggio manuale delle risorse condivise (Scanner, Sessione) tra i vari pacchetti per mantenere basso l'accoppiamento.

---

## 🛠 Roadmap Operativa
1. [x] Configurazione `pom.xml` (MariaDB Driver & JUnit 5).
2. [x] Implementazione `DBConnectionManager` (Singleton).
3. [x] Creazione della gerarchia delle Eccezioni Custom.
4. [x] Implementazione modulo `Boot` e `Orchestrator`.
5. [x] Sviluppo modulo `Login` con Role Switching.
6. [ ] Sviluppo del modulo `Personal Trainer` (Dashboard e Lista Clienti).
7. [ ] Implementazione moduli `Segreteria` e `Cliente`.
