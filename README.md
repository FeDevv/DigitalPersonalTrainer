# Digital Personal Trainer (DPT) 🏋️‍♂️

**Digital Personal Trainer** è un componente software di classe enterprise progettato con un'architettura **Thin-Client**. Sviluppato come modulo operativo per la gestione avanzata degli allenamenti, sposta il baricentro della logica di business e della sicurezza direttamente sul database (MariaDB), garantendo integrità atomica e prestazioni elevate.

Sviluppato per il corso di **Basi di Dati** presso l'**Università degli Studi di Roma "Tor Vergata"**.

---

## 🎯 Obiettivi e Funzionalità
Il sistema automatizza l'intero ciclo di vita dell'allenamento in palestra:
- **Catalogo Rigido**: Gestione di un catalogo chiuso di macchinari ed esercizi (legame 1:1 o corpo libero) per prevenire errori di configurazione.
- **Automazione Workflow**: Popolamento automatico delle serie all'inizio di ogni sessione e calcolo in tempo reale della percentuale di completamento tramite logica procedurale lato DB.
- **Integrità dello Stato**: Archiviazione automatica delle schede precedenti alla creazione di una nuova, garantendo che ogni cliente abbia un unico piano attivo.
- **User Experience Avanzata**: Implementazione del pattern **Guided Selection**, che elimina l'inserimento manuale di ID tecnici a favore di selezioni guidate da liste dinamiche recuperate dal DB.
- **...**
---

## 🏗 Architettura Tecnica

### 1. Database Layer (The "Brain")
Il sistema adotta un approccio **Procedural-First** in cui il DBMS MariaDB non è un semplice contenitore di dati, ma l'esecutore della logica di business:
- **Handshake & Switch (RBAC)**: Sicurezza a livello kernel del DB. L'app effettua un primo aggancio con un utente tecnico e, dopo l'autenticazione, "switcha" la sessione su utenti DBMS specifici per ruolo, sfruttando i `GRANT` nativi.
- **Logica Procedurale**: Utilizzo di **Trigger** con logica XOR (per i macchinari), **Stored Procedures** transazionali per operazioni atomiche e **Cursori** per l'iterazione riga-per-riga durante la generazione delle sessioni.
- **Auto-Manutenzione**: Un **Event Scheduler** (job pianificato) si occupa di chiudere forzatamente le sessioni dimenticate aperte a fine giornata.

### 2. Java Layer (MVC & Design Patterns)
L'applicazione Java (v25) funge da interfaccia intelligente seguendo i principi **SOLID**:
- **Dual-Controller Pattern**: Separazione netta tra la logica di navigazione del modulo (Logic Controller) e l'implementazione della UI (CLI Controller), garantendo l'agnosticismo tecnologico.
- **Strategy Pattern (OCP Compliance)**: L'Orchestrator centrale utilizza una `EnumMap` di strategie funzionali per il dispatching dei moduli, rendendo il sistema estensibile senza modificare il codice core.
- **Fail-Fast Validation**: I **Java Records (DTO)** implementano costruttori compatti che validano i dati all'istante della creazione, impedendo la circolazione di informazioni corrotte tra i layer.
- **CQRS-inspired Reading**: Utilizzo di modelli di lettura denormalizzati (`ActiveSheetItem`) basati su **Viste SQL** per ottimizzare le query e semplificare il mapping JDBC, separando le responsabilità di scrittura e lettura.

---

## 🛠 Stack Tecnologico
- **Core**: Java 25, Maven
- **Persistence**: MariaDB 11.x, JDBC (Pure Access Layer)
- **Testing & Logging**: JUnit 5, SLF4J
- **Pattern**: Singleton (Bill Pugh), Factory Method, Template Method, Strategy, DTO, CQRS (Lite).

---

## 👤 Attori e Ruoli del Sistema
Il sistema implementa un controllo d'accesso granulare basato su 5 identità distinte:
1.  **Login (Ruolo Tecnico)**: Utente con permessi minimi dedicato esclusivamente alla fase di Handshake e verifica iniziale delle credenziali.
2.  **Proprietario**: Amministratore del sistema, gestisce il personale e definisce il catalogo tecnico.
3.  **Segreteria**: Gestisce le anagrafiche dei clienti e l'associazione dinamica tra Atleta e PT di riferimento.
4.  **Personal Trainer**: Redige le schede personalizzate e accede alla reportistica prestazionale specifica per i propri atleti assegnati.
5.  **Cliente**: L'utente finale che esegue l'allenamento, smarca le serie in tempo reale e consulta il proprio storico in sola lettura.

---

## 🚀 Installazione e Esecuzione rapida

### 1. Database Setup
Eseguire lo script SQL unico (`DigitalPersonalTrainer_DB.sql`) sul proprio server MariaDB utilizzando un utente con privilegi amministrativi (es. `root`). Lo script automatizza completamente la configurazione:
- Creazione del database e delle tabelle.
- Definizione della logica procedurale (Trigger, SP, Views, Events).
- Configurazione automatica di tutti gli utenti MariaDB e dei relativi permessi (RBAC).

### 2. Esecuzione
L'applicazione è configurata per operare su `localhost:3306`. Una volta configurato il DB, lanciare i seguenti comandi nella root del progetto:
```bash
mvn clean package
java -jar target/digital-personal-trainer.jar
```

---

## 📝 Note Accademiche
Progetto realizzato per l'esame di **Basi di Dati** - Università degli Studi di Roma "Tor Vergata".
