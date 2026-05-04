package org.DPT;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    public static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

// added 'sp_assegna_pt' in schema.sql
// removed 'trg_disattiva_assegnazione_precedente' in schema.sql
// razionale : trg_disattiva_assegnazione_precedente had to both make an insertion and an update of the same table, this is forbidden in MariaDB
// so a stored procedure has replaced this action, now an insertion in that table is an atomic operation made from 2 units, an update followed by an insetion.

/*
Inizialmente, la gestione dell'unicità dell'assegnazione attiva tra PT e Cliente era stata delegata a un trigger BEFORE INSERT sulla tabella ASSEGNA. Tuttavia, a causa del vincolo di MariaDB
(Errore 1442) che impedisce a un trigger di modificare (UPDATE) la stessa tabella che ne ha causato l'attivazione, si è scelto di migrare tale logica verso la Stored Procedure sp_assegna_pt.

Coerentemente con l'architettura Thin-Client del progetto, la procedura centralizza lato server la logica di business: all'interno di una transazione atomica, provvede a disattivare l'eventuale
assegnazione precedente per il cliente specificato (Assegnazione_Attiva = 0) e a inserire contestualmente il nuovo record come attivo (Assegnazione_Attiva = 1). Questo garantisce l'integrità dei dati
e semplifica il codice Java, che deve invocare un'unica operazione per gestire un flusso complesso.
*/