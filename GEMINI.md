# Digital Personal Trainer - Technical Specification & Engineering Manifesto

## 📖 Panoramica del Progetto
**Digital Personal Trainer (DPT)** è un sistema gestionale per palestre progettato con un'architettura **Thin-Client**. Il cuore della logica di business risiede nel database MariaDB (**Progettazione Procedurale**: Trigger, Stored Procedure, Viste), mentre l'applicazione Java funge da interfaccia intelligente, sicura e performante seguendo i paradigmi **OOP**.

---

## 🎯 Obiettivi Funzionali
1.  **Controllo d'Accesso Granulare**: Gestione di 4 attori (Proprietario, PT, Segreteria, Cliente) con permessi distinti.
2.  **Automazione Workout**: Tracking delle sessioni in tempo reale con calcolo automatico del completamento via DB.
3.  **Integrità del Dato**: Garanzia di coerenza tramite vincoli di dominio e asserzioni procedurali lato server.

---

## 🏗 Architettura del Software

### 1. Pattern Architetturale: MVC (Model-View-Controller)
Il sistema è rigorosamente diviso per separare le responsabilità e predisposto per un'evoluzione **multi-interfaccia (CLI/GUI)**:
*   **Model**: 
    *   **DTO (Data Transfer Objects)**: Uso estensivo di **Java Records** (Java 25) per il trasporto atomico dei dati tra i layer, riducendo l'accoppiamento tra le firme dei metodi.
    *   **DAO (Data Access Object)**: Incapsulamento della logica JDBC interno al pacchetto.
*   **View**: Implementazione attuale tramite CLI interattiva, con astrazioni pronte per l'integrazione di una GUI.
*   **Controller (Pattern Dual-Controller & Agnosticismo UI)**:
    *   **Logic Controller**: Il "cervello" del modulo. Agnostico rispetto alla tecnologia UI, interagisce solo tramite un'interfaccia.
    *   **UI Interface**: Contratto (Interface) che definisce le operazioni di I/O del modulo.
    *   **Local Factory**: Ogni modulo ha una Factory che istanzia l'implementazione UI corretta (CLI o GUI) in base alla configurazione.
    *   **Interface Controller**: Implementazioni concrete dell'interfaccia (es. `LoginCLIController`).

### 2. Orchestration & Global Lifecycle
L'applicazione è governata da un **Orchestrator** centrale che gestisce il ciclo di vita globale:
*   **Package Separation**: Moduli isolati sotto il pacchetto `users` per garantire **High Cohesion**.
*   **Dependency Injection**: Iniezione manuale delle risorse condivise per garantire **Low Coupling**.

### 3. Sicurezza: Architettura "Handshake & Switch" (RBAC)
La sicurezza è delegata al Database MariaDB tramite il meccanismo di Role-Based Access Control:
1.  **Fase di Handshake**: Accesso iniziale con l'utente `dpt_login` (permessi minimi).
2.  **Fase di Switching**: Dopo l'autenticazione, l'app ricollega la sessione usando l'utente MariaDB specifico per il ruolo tramite `DBConnectionManager`.

---

## 💎 Engineering Standards
*   **SOLID & GoF Patterns**: Singleton (Bill Pugh), Factory Locale, Template Method (tramite classi Base).
*   **DRY (Don't Repeat Yourself)**: Astrazione delle logiche CLI comuni in `shared.ui.BaseCLIController` e `shared.ui.BaseCLIView`.
*   **Atomicità dei Dati**: I dati viaggiano esclusivamente tramite DTO completi (Java Records per entità atomiche, Classi per gerarchie).
*   **Data Access Layer Standards**:
    *   **Verticalizzazione**: Un DAO per ogni tabella/entità core.
    *   **Metodi Standard**: Ogni DAO deve implementare `findById`, `getAll` e `findAll(boolean active)` per supportare menu dinamici.
    *   **Mapping Centralizzato**: Ogni DAO utilizza un metodo privato `mapResultSetTo[Entity]` per garantire coerenza e ridurre il boilerplate.
    *   **Verticalizzazione & Purezza**: I DAO devono essere "puri" e verticali (un DAO per tabella/entità). È severamente vietato che un DAO istanzi o utilizzi altri DAO al suo interno per evitare il pattern "Middle Man" e dipendenze circolari.
    *   **Cross-DAO Coordination**: Se una funzionalità richiede l'interazione con più entità (es. il Proprietario che crea un PT), il coordinamento deve avvenire esclusivamente all'interno del **LogicController** di riferimento, che inietterà e orchestrerà i DAO verticali necessari.
    *   **Procedural First**: Se esiste una Stored Procedure nello schema SQL, il DAO *deve* utilizzarla (es. `sp_crea_nuova_scheda`) invece di implementare la logica in Java.
*   **Integrità del Buffer**: Gestione centralizzata del buffer `Scanner` (newline consumption) nei metodi `readInt` della classe base.
*   **Encapsulation**: DAO e implementazioni UI concrete devono preferire lo scope package-private ove possibile.

---

## 📂 Documentazione delle Decisioni
Il file `decisions.txt` funge da **Architecture Decision Log (ADL)**. Ogni scelta progettuale non banale (es. separazione DTO/Model, logica di coordinamento dei controller) deve essere documentata lì con il relativo razionale tecnico. Prima di proporre modifiche strutturali, consulta sempre questo file per comprendere le fondamenta del sistema.
