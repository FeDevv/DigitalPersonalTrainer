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

### 2. Gestione degli Errori: Gerarchia di Eccezioni Custom
Per un controllo totale e un debugging facilitato, implementiamo una gerarchia dedicata:
*   `DPTException` (Base astratta)
    *   `DatabaseException`: Errori di persistenza o violazione vincoli DB.
    *   `AuthException`: Errori di autenticazione o permessi.
    *   `ValidationException`: Input utente non conforme ai requisiti.

### 3. Design Patterns (GoF) & Principi SOLID
*   **Singleton**: `DBConnectionManager` per gestire la connessione (fondamentale per ottimizzare gli 8GB di RAM).
*   **DAO**: Astrazione della persistenza per isolare il codice SQL.
*   **Factory**: Per istanziare dinamicamente i menu e i servizi in base al ruolo dell'utente loggato.
*   **SOLID & DRY**: Codice modulare, manutenibile e privo di duplicazioni, pronto per l'analisi statica.

---

## 💎 Standard di Qualità & SonarQube Readiness
*   **Resource Management**: Uso obbligatorio di `try-with-resources` per ogni risorsa JDBC.
*   **CLI Output**: Uso di `System.out` per la comunicazione con l'utente (accettato come compromesso iniziale per la versione CLI).
*   **SQL Security**: Uso esclusivo di `PreparedStatement` per prevenire SQL Injection.
*   **Clean Code**: Naming convention Java standard, costanti per valori statici e Javadoc per la documentazione della tesi.
*   **Testing**: Implementazione di Unit Test con **JUnit 5** per validare la logica dei controller.

---

## 📁 Struttura dei Package
```text
org.DPT
├── model           # Classi POJO (Entità)
├── persistence     # Interfacce DAO e implementazioni JDBC
├── controller      # Logica di controllo MVC
├── view            # Gestione input/output console
├── exception       # Gerarchia delle eccezioni custom
└── util            # Utility (DBConnectionManager, Configuration)
```

---

## 🛠 Roadmap Operativa
1. [ ] Configurazione `pom.xml` (MariaDB Driver & JUnit 5).
2. [ ] Implementazione `DBConnectionManager` (Singleton).
3. [ ] Creazione della gerarchia delle Eccezioni Custom.
4. [ ] Definizione dei Modelli (POJO) basati sullo schema SQL.
5. [ ] Sviluppo dei primi DAO e test di connessione.
