-- ==============================================================================
-- PROGETTO: Digital Personal Trainer
-- SCRIPT DI SETUP DATABASE: Creazione, Struttura, Logica Procedurale e Popolamento
-- ==============================================================================

-- ==============================================================================
-- 1. CREAZIONE E SELEZIONE DEL DATABASE
-- ==============================================================================

-- Abilita l'esecuzione degli eventi in background (necessario per evt_chiusura_sessioni_timeout)
SET GLOBAL event_scheduler = ON;

-- Creazione idempotente: evita errori se il database esiste già
CREATE DATABASE IF NOT EXISTS digital_personal_trainer;
USE digital_personal_trainer;

-- ==============================================================================
-- 2. CREAZIONE UTENTI (RBAC LATO DB)
-- ==============================================================================

-- 1. Pulizia utenti esistenti (per rendere lo script ri-eseguibile)
DROP USER IF EXISTS 'dpt_login'@'localhost';
DROP USER IF EXISTS 'dpt_proprietario'@'localhost';
DROP USER IF EXISTS 'dpt_pt'@'localhost';
DROP USER IF EXISTS 'dpt_segreteria'@'localhost';
DROP USER IF EXISTS 'dpt_cliente'@'localhost';

-- 2. Creazione Utenti con credenziali allineate a db.properties
CREATE USER 'dpt_login'@'localhost' IDENTIFIED BY 'dpt_login_pwd';
CREATE USER 'dpt_proprietario'@'localhost' IDENTIFIED BY 'dpt_prop_pwd';
CREATE USER 'dpt_pt'@'localhost' IDENTIFIED BY 'dpt_pt_pwd';
CREATE USER 'dpt_segreteria'@'localhost' IDENTIFIED BY 'dpt_seg_pwd';
CREATE USER 'dpt_cliente'@'localhost' IDENTIFIED BY 'dpt_cli_pwd';

-- ==============================================================================
-- 3. PULIZIA DELL'AMBIENTE (DROP OBJECTS)
-- ==============================================================================
-- L'ordine di eliminazione parte dagli oggetti dipendenti (Viste, Eventi, Procedure)
-- per poi passare alle Tabelle in ordine inverso rispetto alle chiavi esterne.

DROP VIEW IF EXISTS vw_prestazioni_pt;
DROP VIEW IF EXISTS vw_scheda_attiva_cliente;
DROP EVENT IF EXISTS evt_chiusura_sessioni_timeout;
DROP PROCEDURE IF EXISTS sp_disattiva_cliente;
DROP PROCEDURE IF EXISTS sp_crea_nuova_scheda;

DROP TABLE IF EXISTS SERIE_ESEGUITA;
DROP TABLE IF EXISTS SESSIONE;
DROP TABLE IF EXISTS COMPOSTA;
DROP TABLE IF EXISTS SCHEDA;
DROP TABLE IF EXISTS ASSEGNA;
DROP TABLE IF EXISTS ESERCIZIO;
DROP TABLE IF EXISTS MACCHINARIO;
DROP TABLE IF EXISTS CLIENTE;
DROP TABLE IF EXISTS ADDETTO_SEGRETERIA;
DROP TABLE IF EXISTS PT;
DROP TABLE IF EXISTS PROPRIETARIO;

-- ==============================================================================
-- 4. CREAZIONE DELLE TABELLE (DDL)
-- ==============================================================================

CREATE TABLE PROPRIETARIO (
                              ID_Proprietario TINYINT UNSIGNED AUTO_INCREMENT,
                              Nome VARCHAR(50) NOT NULL,
                              Cognome VARCHAR(50) NOT NULL,
                              Email VARCHAR(100) NOT NULL,
                              Password VARCHAR(255) NOT NULL,
                              PRIMARY KEY (ID_Proprietario),
                              CONSTRAINT uq_proprietario_email UNIQUE (Email)
) ENGINE=InnoDB;

CREATE TABLE PT (
                    ID_PT SMALLINT UNSIGNED AUTO_INCREMENT,
                    Nome VARCHAR(50) NOT NULL,
                    Cognome VARCHAR(50) NOT NULL,
                    Email VARCHAR(100) NOT NULL,
                    Password VARCHAR(255) NOT NULL,
                    PT_Attivo TINYINT(1) NOT NULL DEFAULT 1,
                    PRIMARY KEY (ID_PT),
                    CONSTRAINT uq_pt_email UNIQUE (Email)
) ENGINE=InnoDB;

CREATE TABLE ADDETTO_SEGRETERIA (
                                    ID_Addetto TINYINT UNSIGNED AUTO_INCREMENT,
                                    Nome VARCHAR(50) NOT NULL,
                                    Cognome VARCHAR(50) NOT NULL,
                                    Email VARCHAR(100) NOT NULL,
                                    Password VARCHAR(255) NOT NULL,
                                    Addetto_Attivo TINYINT(1) NOT NULL DEFAULT 1,
                                    PRIMARY KEY (ID_Addetto),
                                    CONSTRAINT uq_segreteria_email UNIQUE (Email)
) ENGINE=InnoDB;

CREATE TABLE CLIENTE (
                         ID_Cliente MEDIUMINT UNSIGNED AUTO_INCREMENT,
                         Nome VARCHAR(50) NOT NULL,
                         Cognome VARCHAR(50) NOT NULL,
                         Email VARCHAR(100) NOT NULL,
                         Password VARCHAR(255) NOT NULL,
                         Codice_Fiscale CHAR(16) NOT NULL,
                         Indirizzo_Residenza VARCHAR(150),
                         Data_Nascita DATE NOT NULL,
                         Cliente_Attivo TINYINT(1) NOT NULL DEFAULT 1,
                         PRIMARY KEY (ID_Cliente),
                         CONSTRAINT uq_cliente_email UNIQUE (Email),
                         CONSTRAINT uq_cliente_cf UNIQUE (Codice_Fiscale)
) ENGINE=InnoDB;

CREATE TABLE MACCHINARIO (
                             ID_Macchinario SMALLINT UNSIGNED AUTO_INCREMENT,
                             ID_Proprietario TINYINT UNSIGNED NOT NULL,
                             Nome VARCHAR(100) NOT NULL,
                             Descrizione_Macchinario TEXT,
                             Macchinario_Attivo TINYINT(1) NOT NULL DEFAULT 1,
                             PRIMARY KEY (ID_Macchinario),
                             CONSTRAINT fk_macchinario_proprietario
                                 FOREIGN KEY (ID_Proprietario) REFERENCES PROPRIETARIO(ID_Proprietario)
                                     ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE ESERCIZIO (
                           Codice_Esercizio SMALLINT UNSIGNED AUTO_INCREMENT,
                           ID_Proprietario TINYINT UNSIGNED NOT NULL,
                           ID_Macchinario SMALLINT UNSIGNED NULL,
                           Nome VARCHAR(100) NOT NULL,
                           Descrizione_Esercizio TEXT,
                           Corpo_Libero TINYINT(1) NOT NULL DEFAULT 0,
                           Esercizio_Attivo TINYINT(1) NOT NULL DEFAULT 1,
                           PRIMARY KEY (Codice_Esercizio),
                           CONSTRAINT fk_esercizio_proprietario
                               FOREIGN KEY (ID_Proprietario) REFERENCES PROPRIETARIO(ID_Proprietario)
                                   ON UPDATE CASCADE ON DELETE RESTRICT,
                           CONSTRAINT fk_esercizio_macchinario
                               FOREIGN KEY (ID_Macchinario) REFERENCES MACCHINARIO(ID_Macchinario)
                                   ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE ASSEGNA (
                         ID_PT SMALLINT UNSIGNED NOT NULL,
                         ID_Cliente MEDIUMINT UNSIGNED NOT NULL,
                         ID_Addetto TINYINT UNSIGNED NOT NULL,
                         PRIMARY KEY (ID_PT, ID_Cliente, ID_Addetto),
                         CONSTRAINT fk_assegna_pt
                             FOREIGN KEY (ID_PT) REFERENCES PT(ID_PT)
                                 ON UPDATE CASCADE ON DELETE RESTRICT,
                         CONSTRAINT fk_assegna_cliente
                             FOREIGN KEY (ID_Cliente) REFERENCES CLIENTE(ID_Cliente)
                                 ON UPDATE CASCADE ON DELETE RESTRICT,
                         CONSTRAINT fk_assegna_addetto
                             FOREIGN KEY (ID_Addetto) REFERENCES ADDETTO_SEGRETERIA(ID_Addetto)
                                 ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE SCHEDA (
                        ID_Scheda MEDIUMINT UNSIGNED AUTO_INCREMENT,
                        ID_PT SMALLINT UNSIGNED NOT NULL,
                        ID_Cliente MEDIUMINT UNSIGNED NOT NULL,
                        Data_Creazione DATE NOT NULL,
                        Titolo VARCHAR(100) NOT NULL,
                        Scheda_Attiva TINYINT(1) NOT NULL DEFAULT 1,
                        Totale_Serie_Previste SMALLINT UNSIGNED NOT NULL DEFAULT 0,
                        PRIMARY KEY (ID_Scheda),
                        CONSTRAINT fk_scheda_pt
                            FOREIGN KEY (ID_PT) REFERENCES PT(ID_PT)
                                ON UPDATE CASCADE ON DELETE RESTRICT,
                        CONSTRAINT fk_scheda_cliente
                            FOREIGN KEY (ID_Cliente) REFERENCES CLIENTE(ID_Cliente)
                                ON UPDATE CASCADE ON DELETE RESTRICT,
                        INDEX idx_scheda_cliente_attiva (ID_Cliente, Scheda_Attiva)
) ENGINE=InnoDB;

CREATE TABLE COMPOSTA (
                          ID_Scheda MEDIUMINT UNSIGNED NOT NULL,
                          Codice_Esercizio SMALLINT UNSIGNED NOT NULL,
                          Recupero SMALLINT UNSIGNED NOT NULL,
                          Note_Esecuzione TEXT,
                          Serie_Previste TINYINT UNSIGNED NOT NULL,
                          Ripetizioni_Previste TINYINT UNSIGNED NOT NULL,
                          PRIMARY KEY (ID_Scheda, Codice_Esercizio),
                          CONSTRAINT fk_composta_scheda
                              FOREIGN KEY (ID_Scheda) REFERENCES SCHEDA(ID_Scheda)
                                  ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_composta_esercizio
                              FOREIGN KEY (Codice_Esercizio) REFERENCES ESERCIZIO(Codice_Esercizio)
                                  ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE SESSIONE (
                          ID_Sessione INT UNSIGNED AUTO_INCREMENT,
                          ID_Scheda MEDIUMINT UNSIGNED NOT NULL,
                          Data DATE NOT NULL,
                          Ora_Inizio TIME NOT NULL,
                          Ora_Fine TIME NULL,
                          Percentuale_Completamento TINYINT UNSIGNED NOT NULL DEFAULT 0,
                          PRIMARY KEY (ID_Sessione),
                          CONSTRAINT fk_sessione_scheda
                              FOREIGN KEY (ID_Scheda) REFERENCES SCHEDA(ID_Scheda)
                                  ON UPDATE CASCADE ON DELETE RESTRICT,
                          INDEX idx_sessione_data (Data)
) ENGINE=InnoDB;

CREATE TABLE SERIE_ESEGUITA (
                                ID_Sessione INT UNSIGNED NOT NULL,
                                Codice_Esercizio SMALLINT UNSIGNED NOT NULL,
                                Numero_Serie TINYINT UNSIGNED NOT NULL,
                                Carico_Effettivo DECIMAL(5,2) UNSIGNED,
                                Completata TINYINT(1) NOT NULL DEFAULT 0,
                                PRIMARY KEY (ID_Sessione, Codice_Esercizio, Numero_Serie),
                                CONSTRAINT fk_serie_sessione
                                    FOREIGN KEY (ID_Sessione) REFERENCES SESSIONE(ID_Sessione)
                                        ON UPDATE CASCADE ON DELETE RESTRICT,
                                CONSTRAINT fk_serie_esercizio
                                    FOREIGN KEY (Codice_Esercizio) REFERENCES ESERCIZIO(Codice_Esercizio)
                                        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ==============================================================================
-- 5. VISTE (VIEWS)
-- ==============================================================================

CREATE VIEW vw_prestazioni_pt AS
SELECT
    a.ID_PT,
    c.ID_Cliente,
    CONCAT(c.Nome, ' ', c.Cognome) AS Nominativo_Cliente,
    sess.ID_Sessione,
    sess.Data,
    sess.Ora_Inizio,
    sess.Ora_Fine,
    sess.Percentuale_Completamento,
    TIMESTAMPDIFF(MINUTE, sess.Ora_Inizio, sess.Ora_Fine) AS Durata_Minuti
FROM ASSEGNA a
         JOIN CLIENTE c ON a.ID_Cliente = c.ID_Cliente
         JOIN SCHEDA sch ON c.ID_Cliente = sch.ID_Cliente AND a.ID_PT = sch.ID_PT
         JOIN SESSIONE sess ON sch.ID_Scheda = sess.ID_Scheda;

CREATE VIEW vw_scheda_attiva_cliente AS
SELECT
    c.ID_Cliente,
    sch.ID_Scheda,
    sch.Titolo AS Nome_Scheda,
    comp.Codice_Esercizio,
    e.Nome AS Nome_Esercizio,
    comp.Serie_Previste,
    comp.Ripetizioni_Previste,
    comp.Recupero,
    comp.Note_Esecuzione,
    e.Corpo_Libero
FROM CLIENTE c
         JOIN SCHEDA sch ON c.ID_Cliente = sch.ID_Cliente AND sch.Scheda_Attiva = 1
         JOIN COMPOSTA comp ON sch.ID_Scheda = comp.ID_Scheda
         JOIN ESERCIZIO e ON comp.Codice_Esercizio = e.Codice_Esercizio;

-- ==============================================================================
-- 6. TRIGGER PER LOGICA DI BUSINESS AVANZATA
-- ==============================================================================

DELIMITER //

CREATE TRIGGER trg_chk_corpo_libero_insert
    BEFORE INSERT ON ESERCIZIO
    FOR EACH ROW
BEGIN
    IF (NEW.Corpo_Libero = 1 AND NEW.ID_Macchinario IS NOT NULL) OR
       (NEW.Corpo_Libero = 0 AND NEW.ID_Macchinario IS NULL) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Violazione: Un esercizio a corpo libero non puo avere un macchinario (e viceversa).';
    END IF;
END //

CREATE TRIGGER trg_chk_corpo_libero_update
    BEFORE UPDATE ON ESERCIZIO
    FOR EACH ROW
BEGIN
    IF (NEW.Corpo_Libero = 1 AND NEW.ID_Macchinario IS NOT NULL) OR
       (NEW.Corpo_Libero = 0 AND NEW.ID_Macchinario IS NULL) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Violazione: Un esercizio a corpo libero non puo avere un macchinario (e viceversa).';
    END IF;
END //

CREATE TRIGGER trg_check_orari_sessione_insert
    BEFORE INSERT ON SESSIONE
    FOR EACH ROW
BEGIN
    IF NEW.Ora_Fine IS NOT NULL AND NEW.Ora_Fine < NEW.Ora_Inizio THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrità Violata: L''ora di fine sessione non puo essere antecedente all''ora di inizio.';
    END IF;
END //

CREATE TRIGGER trg_check_orari_sessione_update
    BEFORE UPDATE ON SESSIONE
    FOR EACH ROW
BEGIN
    IF NEW.Ora_Fine IS NOT NULL AND NEW.Ora_Fine < NEW.Ora_Inizio THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrità Violata: L''ora di fine sessione non puo essere antecedente all''ora di inizio.';
    END IF;
END //

CREATE TRIGGER trg_check_carico_positivo_insert
    BEFORE INSERT ON SERIE_ESEGUITA
    FOR EACH ROW
BEGIN
    IF NEW.Carico_Effettivo < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrità Violata: Il carico effettivo non puo assumere un valore negativo.';
    END IF;
END //

CREATE TRIGGER trg_check_carico_positivo_update
    BEFORE UPDATE ON SERIE_ESEGUITA
    FOR EACH ROW
BEGIN
    IF NEW.Carico_Effettivo < 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrità Violata: Il carico effettivo non puo assumere un valore negativo.';
    END IF;
END //

CREATE TRIGGER trg_check_data_nascita_insert
    BEFORE INSERT ON CLIENTE
    FOR EACH ROW
BEGIN
    IF NEW.Data_Nascita > CURRENT_DATE() - INTERVAL 14 YEAR THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Integrità Violata: Il cliente deve avere almeno 14 anni o data non valida.';
    END IF;
END //

CREATE TRIGGER trg_aggiorna_percentuale_update
    AFTER UPDATE ON SERIE_ESEGUITA
    FOR EACH ROW
BEGIN
    DECLARE v_totale_serie_previste SMALLINT;
    DECLARE v_serie_completate SMALLINT;
    DECLARE v_nuova_percentuale TINYINT;

    SELECT s.Totale_Serie_Previste INTO v_totale_serie_previste
    FROM SESSIONE se
             JOIN SCHEDA s ON se.ID_Scheda = s.ID_Scheda
    WHERE se.ID_Sessione = NEW.ID_Sessione;

    IF v_totale_serie_previste > 0 THEN
        SELECT COUNT(*) INTO v_serie_completate
        FROM SERIE_ESEGUITA
        WHERE ID_Sessione = NEW.ID_Sessione AND Completata = 1;

        SET v_nuova_percentuale = (v_serie_completate * 100) / v_totale_serie_previste;

        UPDATE SESSIONE
        SET Percentuale_Completamento = v_nuova_percentuale
        WHERE ID_Sessione = NEW.ID_Sessione;
    END IF;
END //

CREATE TRIGGER trg_aggiorna_totale_serie_insert
    AFTER INSERT ON COMPOSTA
    FOR EACH ROW
BEGIN
    UPDATE SCHEDA
    SET Totale_Serie_Previste = (
        SELECT IFNULL(SUM(Serie_Previste), 0)
        FROM COMPOSTA
        WHERE ID_Scheda = NEW.ID_Scheda
    )
    WHERE ID_Scheda = NEW.ID_Scheda;
END //

CREATE TRIGGER trg_aggiorna_totale_serie_update
    AFTER UPDATE ON COMPOSTA
    FOR EACH ROW
BEGIN
    UPDATE SCHEDA
    SET Totale_Serie_Previste = (
        SELECT IFNULL(SUM(Serie_Previste), 0)
        FROM COMPOSTA
        WHERE ID_Scheda = NEW.ID_Scheda
    )
    WHERE ID_Scheda = NEW.ID_Scheda;
END //

CREATE TRIGGER trg_aggiorna_totale_serie_delete
    AFTER DELETE ON COMPOSTA
    FOR EACH ROW
BEGIN
    UPDATE SCHEDA
    SET Totale_Serie_Previste = (
        SELECT IFNULL(SUM(Serie_Previste), 0)
        FROM COMPOSTA
        WHERE ID_Scheda = OLD.ID_Scheda
    )
    WHERE ID_Scheda = OLD.ID_Scheda;
END //

CREATE TRIGGER trg_popola_serie_sessione
    AFTER INSERT ON SESSIONE
    FOR EACH ROW
BEGIN
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_codice_esercizio SMALLINT;
    DECLARE v_serie_previste TINYINT;
    DECLARE v_contatore INT;

    DECLARE cur_esercizi CURSOR FOR
        SELECT Codice_Esercizio, Serie_Previste
        FROM COMPOSTA
        WHERE ID_Scheda = NEW.ID_Scheda;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    OPEN cur_esercizi;

    read_loop: LOOP
        FETCH cur_esercizi INTO v_codice_esercizio, v_serie_previste;

        IF v_done THEN
            LEAVE read_loop;
        END IF;

        SET v_contatore = 1;
        WHILE v_contatore <= v_serie_previste DO
                INSERT INTO SERIE_ESEGUITA (ID_Sessione, Codice_Esercizio, Numero_Serie, Carico_Effettivo, Completata)
                VALUES (NEW.ID_Sessione, v_codice_esercizio, v_contatore, NULL, 0);

                SET v_contatore = v_contatore + 1;
            END WHILE;

    END LOOP;

    CLOSE cur_esercizi;
END //

CREATE TRIGGER trg_check_pt_cliente_assegnati_insert
    BEFORE INSERT ON SCHEDA
    FOR EACH ROW
BEGIN
    DECLARE v_esiste_assegnazione INT;
    DECLARE v_pt_attivo TINYINT;
    DECLARE v_cliente_attivo TINYINT;

    SELECT COUNT(*) INTO v_esiste_assegnazione
    FROM ASSEGNA
    WHERE ID_PT = NEW.ID_PT AND ID_Cliente = NEW.ID_Cliente;

    IF v_esiste_assegnazione = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Sicurezza: Assegnazione inesistente. Il PT non e autorizzato a seguire questo Cliente.';
    END IF;

    SELECT PT_Attivo INTO v_pt_attivo FROM PT WHERE ID_PT = NEW.ID_PT;
    SELECT Cliente_Attivo INTO v_cliente_attivo FROM CLIENTE WHERE ID_Cliente = NEW.ID_Cliente;

    IF v_pt_attivo = 0 OR v_cliente_attivo = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Sicurezza: Impossibile creare scheda. Il PT o il Cliente sono contrassegnati come disattivati.';
    END IF;
END //

CREATE TRIGGER trg_blocco_modifica_sessione_chiusa_update
    BEFORE UPDATE ON SERIE_ESEGUITA
    FOR EACH ROW
BEGIN
    DECLARE v_ora_fine TIME;
    DECLARE v_data DATE;

    SELECT Ora_Fine, Data INTO v_ora_fine, v_data
    FROM SESSIONE
    WHERE ID_Sessione = NEW.ID_Sessione;

    IF v_ora_fine IS NOT NULL OR v_data < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Sicurezza: La sessione è chiusa o appartiene al passato. Impossibile alterare le serie eseguite.';
    END IF;
END //

CREATE TRIGGER trg_blocco_modifica_sessione_chiusa_delete
    BEFORE DELETE ON SERIE_ESEGUITA
    FOR EACH ROW
BEGIN
    DECLARE v_ora_fine TIME;
    DECLARE v_data DATE;

    SELECT Ora_Fine, Data INTO v_ora_fine, v_data
    FROM SESSIONE
    WHERE ID_Sessione = OLD.ID_Sessione;

    IF v_ora_fine IS NOT NULL OR v_data < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Sicurezza: La sessione è chiusa o appartiene al passato. Impossibile eliminare le serie eseguite.';
    END IF;
END //

DELIMITER ;

-- ==============================================================================
-- 7. PROCEDURE ED EVENTI (LOGICA PROCEDURALE)
-- ==============================================================================

DELIMITER //

CREATE EVENT evt_chiusura_sessioni_timeout
    ON SCHEDULE EVERY 1 DAY
        STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 23 HOUR + INTERVAL 59 MINUTE)
    DO
    BEGIN
        UPDATE SESSIONE sess
        SET Percentuale_Completamento = (
            SELECT IFNULL(ROUND((SUM(Completata) / MAX(sch.Totale_Serie_Previste)) * 100), 0)
            FROM SERIE_ESEGUITA se
                     JOIN SCHEDA sch ON sess.ID_Scheda = sch.ID_Scheda
            WHERE se.ID_Sessione = sess.ID_Sessione
        )
        WHERE sess.Data = CURRENT_DATE
          AND sess.Ora_Fine IS NULL;
    END //

CREATE PROCEDURE sp_disattiva_cliente(
    IN p_ID_Cliente MEDIUMINT UNSIGNED
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    UPDATE CLIENTE
    SET Cliente_Attivo = 0
    WHERE ID_Cliente = p_ID_Cliente;

    UPDATE SCHEDA
    SET Scheda_Attiva = 0
    WHERE ID_Cliente = p_ID_Cliente AND Scheda_Attiva = 1;

    COMMIT;
END //

CREATE PROCEDURE sp_crea_nuova_scheda(
    IN p_ID_PT SMALLINT UNSIGNED,
    IN p_ID_Cliente MEDIUMINT UNSIGNED,
    IN p_Titolo VARCHAR(100),
    IN p_Totale_Serie SMALLINT UNSIGNED
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    START TRANSACTION;

    UPDATE SCHEDA
    SET Scheda_Attiva = 0
    WHERE ID_Cliente = p_ID_Cliente AND Scheda_Attiva = 1;

    INSERT INTO SCHEDA (ID_PT, ID_Cliente, Data_Creazione, Titolo, Scheda_Attiva, Totale_Serie_Previste)
    VALUES (p_ID_PT, p_ID_Cliente, CURRENT_DATE, p_Titolo, 1, p_Totale_Serie);

    COMMIT;
END //

DELIMITER ;

-- ==============================================================================
-- 8. ASSEGNAZIONE PRIVILEGI (RBAC FINALE)
-- ==============================================================================

-- [LOGIN]: Sola lettura delle credenziali e info minime per la sessione
GRANT SELECT (ID_Proprietario, Nome, Cognome, Email, Password) ON digital_personal_trainer.PROPRIETARIO TO 'dpt_login'@'localhost';
GRANT SELECT (ID_PT, Nome, Cognome, Email, Password, PT_Attivo) ON digital_personal_trainer.PT TO 'dpt_login'@'localhost';
GRANT SELECT (ID_Addetto, Nome, Cognome, Email, Password, Addetto_Attivo) ON digital_personal_trainer.ADDETTO_SEGRETERIA TO 'dpt_login'@'localhost';
GRANT SELECT (ID_Cliente, Nome, Cognome, Email, Password, Cliente_Attivo) ON digital_personal_trainer.CLIENTE TO 'dpt_login'@'localhost';

-- [OWNER]: Gestione del personale e del catalogo
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.PROPRIETARIO TO 'dpt_proprietario'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.PT TO 'dpt_proprietario'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.ADDETTO_SEGRETERIA TO 'dpt_proprietario'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.CLIENTE TO 'dpt_proprietario'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.MACCHINARIO TO 'dpt_proprietario'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.ESERCIZIO TO 'dpt_proprietario'@'localhost';
GRANT EXECUTE ON PROCEDURE digital_personal_trainer.sp_disattiva_cliente TO 'dpt_proprietario'@'localhost';

-- [PT]: Gestione schede e monitoraggio
GRANT SELECT ON digital_personal_trainer.CLIENTE TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.PT TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.MACCHINARIO TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.ESERCIZIO TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.ASSEGNA TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.SESSIONE TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.SERIE_ESEGUITA TO 'dpt_pt'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.SCHEDA TO 'dpt_pt'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON digital_personal_trainer.COMPOSTA TO 'dpt_pt'@'localhost';
GRANT SELECT ON digital_personal_trainer.vw_prestazioni_pt TO 'dpt_pt'@'localhost';
GRANT EXECUTE ON PROCEDURE digital_personal_trainer.sp_crea_nuova_scheda TO 'dpt_pt'@'localhost';

-- [RECEPTIONIST]: Gestione anagrafiche e assegnazioni
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.CLIENTE TO 'dpt_segreteria'@'localhost';
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.PT TO 'dpt_segreteria'@'localhost';
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.ADDETTO_SEGRETERIA TO 'dpt_segreteria'@'localhost';
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.ASSEGNA TO 'dpt_segreteria'@'localhost';
GRANT EXECUTE ON PROCEDURE digital_personal_trainer.sp_disattiva_cliente TO 'dpt_segreteria'@'localhost';

-- [CLIENT]: Operatività limitata al proprio allenamento
GRANT SELECT ON digital_personal_trainer.vw_scheda_attiva_cliente TO 'dpt_cliente'@'localhost';
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.SESSIONE TO 'dpt_cliente'@'localhost';
GRANT SELECT, INSERT, UPDATE ON digital_personal_trainer.SERIE_ESEGUITA TO 'dpt_cliente'@'localhost';

FLUSH PRIVILEGES;
