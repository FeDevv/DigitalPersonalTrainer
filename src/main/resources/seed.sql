-- ==============================================================================
-- PROGETTO: Digital Personal Trainer
-- SCRIPT: seed.sql
-- DESCRIZIONE: Popolamento del database con dataset ridotto per ambiente demo.
-- DIMENSIONI: 1 Admin, 3 Segreteria, 5 PT, 8 Clienti.
-- ==============================================================================

USE digital_personal_trainer;

-- ==============================================================================
-- 1. POPOLAMENTO TABELLE ANAGRAFICHE
-- ==============================================================================

-- 1.1 Inserimento record tabella OWNER
INSERT INTO PROPRIETARIO (Nome, Cognome, Email, Password) VALUES
    ('Roberto', 'Ferrari', 'admin@digitalpt.it', 'password123');

-- 1.2 Inserimento record tabella ADDETTO_SEGRETERIA (3 Addetti)
INSERT INTO ADDETTO_SEGRETERIA (Nome, Cognome, Email, Password, Addetto_Attivo) VALUES
                                                                                    ('Giulia', 'Verdi', 'giulia@digitalpt.it', 'password123', 1),
                                                                                    ('Matteo', 'Neri', 'matteo@digitalpt.it', 'password123', 1),
                                                                                    ('Chiara', 'Gialli', 'chiara@digitalpt.it', 'password123', 1);

-- 1.3 Inserimento record tabella PT (5 Personal Trainer)
INSERT INTO PT (Nome, Cognome, Email, Password, PT_Attivo) VALUES
                                                               ('Luca', 'Spartani', 'luca.pt@digitalpt.it', 'password123', 1),
                                                               ('Anna', 'Fitness', 'anna.pt@digitalpt.it', 'password123', 1),
                                                               ('Marco', 'Ghisa', 'marco.pt@digitalpt.it', 'password123', 1),
                                                               ('Elena', 'Cardio', 'elena.pt@digitalpt.it', 'password123', 1),
                                                               ('Sara', 'Storico', 'sara.pt@digitalpt.it', 'password123', 1);

-- 1.4 Inserimento record tabella CLIENT (8 Clienti)
-- Stato iniziale imposto a 1 per consentire il superamento dei controlli logici.
INSERT INTO CLIENTE (Nome, Cognome, Email, Password, Codice_Fiscale, Indirizzo_Residenza, Data_Nascita, Cliente_Attivo) VALUES
                                                                                                                            ('Andrea', 'Rossi', 'andrea@email.it', 'password123', 'RSSNDR90A01H501A', 'Via Roma 1, Milano', '1990-05-10', 1),
                                                                                                                            ('Beatrice', 'Bianchi', 'bea@email.it', 'password123', 'BNCBTR95M45H501B', 'Via Milano 2, Roma', '1995-08-20', 1),
                                                                                                                            ('Carlo', 'Verdi', 'carlo@email.it', 'password123', 'VRDCRL85C15H501C', 'Via Napoli 3, Napoli', '1985-03-12', 1),
                                                                                                                            ('Davide', 'Neri', 'davide@email.it', 'password123', 'NRIDVD92E10H501D', 'Via Torino 4, Torino', '1992-11-05', 1),
                                                                                                                            ('Elisa', 'Gialli', 'elisa@email.it', 'password123', 'GLLELS88H50H501E', 'Via Firenze 5, Firenze', '1988-07-22', 1),
                                                                                                                            ('Fabio', 'Marrone', 'fabio@email.it', 'password123', 'MRRFBO98P01H501F', 'Via Genova 6, Genova', '1998-09-30', 1),
                                                                                                                            ('Giorgia', 'Blu', 'giorgia@email.it', 'password123', 'BLUGRG93R45H501G', 'Via Venezia 7, Venezia', '1993-01-18', 1),
                                                                                                                            ('Ilaria', 'Viola', 'ilaria@email.it', 'password123', 'VLILRA89T60H501H', 'Via Bari 8, Bari', '1989-12-05', 1);

-- ==============================================================================
-- 2. POPOLAMENTO CATALOGO
-- ==============================================================================

-- 2.1 Inserimento record tabella MACCHINARIO
INSERT INTO MACCHINARIO (ID_Proprietario, Nome, Descrizione_Macchinario, Macchinario_Attivo) VALUES
                                                                                                 (1, 'Panca Piana Olimpionica', 'Panca per bilanciere fissa', 1),
                                                                                                 (1, 'Leg Press 45', 'Pressa a 45 gradi', 1),
                                                                                                 (1, 'Lat Machine', 'Trazioni dall''alto a cavo', 1);

-- 2.2 Inserimento record tabella ESERCIZIO
INSERT INTO ESERCIZIO (ID_Proprietario, ID_Macchinario, Nome, Descrizione_Esercizio, Corpo_Libero, Esercizio_Attivo) VALUES
                                                                                                                         (1, 1, 'Panca Piana con Bilanciere', 'Pettorali completi', 0, 1),
                                                                                                                         (1, 2, 'Leg Press 45', 'Gambe e glutei', 0, 1),
                                                                                                                         (1, 3, 'Lat Machine Presa Larga', 'Dorsali', 0, 1),
                                                                                                                         (1, NULL, 'Push Up', 'Piegamenti classici sulle braccia', 1, 1),
                                                                                                                         (1, NULL, 'Plank', 'Isometria addome', 1, 1);

-- ==============================================================================
-- 3. ASSEGNAZIONI
-- ==============================================================================

-- 3.1 Inserimento record tabella ASSEGNA (Relazioni PT - Cliente - Segreteria)
INSERT INTO ASSEGNA (ID_PT, ID_Cliente, ID_Addetto) VALUES
                                                        (1, 1, 1), (1, 2, 1),
                                                        (2, 3, 2), (2, 4, 2),
                                                        (3, 5, 1), (4, 6, 2),
                                                        (5, 7, 3), (5, 8, 3);

-- ==============================================================================
-- 4. CREAZIONE SCHEDE DI ALLENAMENTO
-- ==============================================================================

-- 4.1 Inserimento record tabella SCHEDA
-- Inizializzazione univoca: Massimo 1 scheda attiva per cliente.
INSERT INTO SCHEDA (ID_PT, ID_Cliente, Data_Creazione, Titolo, Scheda_Attiva) VALUES
                                                                                  (1, 1, CURRENT_DATE - INTERVAL 40 DAY, 'Ricondizionamento', 0),  -- Storica (Cliente 1)
                                                                                  (1, 1, CURRENT_DATE - INTERVAL 5 DAY, 'Ipertrofia Base', 1),     -- Attiva (Cliente 1)
                                                                                  (1, 2, CURRENT_DATE - INTERVAL 10 DAY, 'Forza Base', 1),         -- Attiva (Cliente 2)
                                                                                  (2, 3, CURRENT_DATE - INTERVAL 15 DAY, 'Tonificazione V1', 1),   -- Attiva (Cliente 3)
                                                                                  (2, 4, CURRENT_DATE - INTERVAL 8 DAY, 'Total Body', 1),          -- Attiva (Cliente 4)
                                                                                  (3, 5, CURRENT_DATE - INTERVAL 12 DAY, 'Upper Body', 1),         -- Attiva (Cliente 5)
                                                                                  (4, 6, CURRENT_DATE - INTERVAL 20 DAY, 'Dimagrimento LISS', 1),  -- Attiva (Cliente 6)
                                                                                  (5, 7, CURRENT_DATE - INTERVAL 60 DAY, 'Scheda Abbandonata 1', 0),-- Inattiva (Cliente 7)
                                                                                  (5, 8, CURRENT_DATE - INTERVAL 55 DAY, 'Scheda Abbandonata 2', 0);-- Inattiva (Cliente 8)

-- 4.2 Inserimento record tabella COMPOSTA (Assegnazione esplicita per evitare lock)
INSERT INTO COMPOSTA (ID_Scheda, Codice_Esercizio, Recupero, Note_Esecuzione, Serie_Previste, Ripetizioni_Previste) VALUES
-- Scheda 1 (ID 1 - Storica Cliente 1)
(1, 4, 60, 'Corpo libero', 3, 10), (1, 5, 60, 'Massima tenuta', 3, 1),
-- Scheda 2 (ID 2 - Attiva Cliente 1)
(2, 1, 120, 'Fermo al petto', 4, 8), (2, 3, 90, 'Trazione completa', 4, 10),
-- Scheda 3 (ID 3 - Attiva Cliente 2)
(3, 2, 120, 'Piedi larghi', 5, 5), (3, 4, 60, 'Sfinimento', 3, 15),
-- Scheda 4 (ID 4 - Attiva Cliente 3)
(4, 2, 90, 'Controllare la discesa', 3, 12), (4, 5, 45, 'Core duro', 4, 1),
-- Scheda 5 (ID 5 - Attiva Cliente 4)
(5, 1, 90, 'Carico medio', 3, 10), (5, 2, 90, 'Carico medio', 3, 10),
-- Scheda 6 (ID 6 - Attiva Cliente 5)
(6, 1, 120, 'Spinta forte', 4, 6), (6, 3, 90, 'Dorso contratto', 4, 8),
-- Scheda 7 (ID 7 - Attiva Cliente 6)
(7, 4, 45, 'Veloce', 4, 15), (7, 5, 30, 'Recupero breve', 4, 1),
-- Schede 8 e 9 (Storiche Clienti 7 e 8)
(8, 2, 90, 'Base', 3, 10), (9, 3, 90, 'Base', 3, 10);

-- ==============================================================================
-- 5. POPOLAMENTO TRANSAZIONALE (SESSIONI E SERIE)
-- ==============================================================================

-- 5.1 Inizializzazione record tabella SESSIONE in data odierna
INSERT INTO SESSIONE (ID_Scheda, Data, Ora_Inizio, Ora_Fine) VALUES
                                                                 (1, CURRENT_DATE, '10:00:00', NULL), -- Sessione per Scheda 1 (Storica)
                                                                 (2, CURRENT_DATE, '17:00:00', NULL), -- Sessione per Scheda 2 (Attiva, resterà aperta)
                                                                 (3, CURRENT_DATE, '18:00:00', NULL), -- Sessione per Scheda 3
                                                                 (8, CURRENT_DATE, '09:00:00', NULL), -- Sessione per Scheda 8 (Abbandonata)
                                                                 (9, CURRENT_DATE, '09:30:00', NULL); -- Sessione per Scheda 9 (Abbandonata)

-- 5.2 Aggiornamento dati operativi tabella SERIE_ESEGUITA (Auto-popolata)
UPDATE SERIE_ESEGUITA
SET Completata = 1,
    Carico_Effettivo = FLOOR(RAND() * (80 - 20 + 1)) + 20
WHERE ID_Sessione IN (1, 2, 3, 4, 5) AND RAND() < 0.90;

-- 5.3 Normalizzazione dati tabella SERIE_ESEGUITA (Corpo Libero)
UPDATE SERIE_ESEGUITA se
    JOIN ESERCIZIO e ON se.Codice_Esercizio = e.Codice_Esercizio
SET se.Carico_Effettivo = NULL
WHERE e.Corpo_Libero = 1;

-- ==============================================================================
-- 6. STORICIZZAZIONE DATI E APPLICAZIONE STATI LOGICI
-- ==============================================================================

-- 6.1 Retrodatazione sessioni completate
UPDATE SESSIONE SET Data = CURRENT_DATE - INTERVAL 35 DAY, Ora_Fine = '11:00:00' WHERE ID_Sessione = 1;
-- La sessione ID 2 è mantenuta nello stato corrente (oggi, senza ora_fine).
UPDATE SESSIONE SET Data = CURRENT_DATE - INTERVAL 2 DAY, Ora_Fine = '19:30:00' WHERE ID_Sessione = 3;
UPDATE SESSIONE SET Data = CURRENT_DATE - INTERVAL 58 DAY, Ora_Fine = '10:15:00' WHERE ID_Sessione = 4;
UPDATE SESSIONE SET Data = CURRENT_DATE - INTERVAL 50 DAY, Ora_Fine = '11:00:00' WHERE ID_Sessione = 5;

-- 6.2 Disattivazione logica operatori e clienti storici
UPDATE ADDETTO_SEGRETERIA SET Addetto_Attivo = 0 WHERE ID_Addetto = 3;
UPDATE PT SET PT_Attivo = 0 WHERE ID_PT = 5;
UPDATE CLIENTE SET Cliente_Attivo = 0 WHERE ID_Cliente IN (7, 8);

-- ==============================================================================
-- FINE SCRIPT DI SEEDING
-- ==============================================================================