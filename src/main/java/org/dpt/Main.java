package org.dpt;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

// check CODICE IN INGLESE, OUTPUT IN ITALIANO -> in TRADUZIONE.txt le parti da rivedere
// ⚠️check commenti!!

// TODO: quando si aggiornano alcune utenze, non compare il messaggio "[*] OK: Stato cliente aggiornato." - problema centralizzato (succede su tutte le utenze, quindi basta cambiare la classe che causa il problema)
// TODO: far si che al logout si torni al login, creare conseguentemente l'opzione di uscita esplicita "esci" (all'interno del menù dell'utenza specifica) - da implementare

/*
 se un addetto di segreteria si disattiva da solo, può continuare a girare nel sistema fino al riavvio,
 momento al quale non potrà fare l'accesso - locked out.
 - buttarlo fuori subito?
 - dargli la possibilità di finire il turno?
 scelta sul come comportarsi.
*/

/*
 pensare di gestire diversamente i pacchetti shared - elementi come "auth" e "context" potrebbero
 essere tirati fuori, messi allo stesso livello di "boot", mentre gli altri elementi potrebbero
 venir lasciati all'interno di "shared" e magari riorganizzati in sotto-pacchetti.
*/

// To resume this session: gemini --resume 6987a896-4a04-4c6c-bbf2-7ff9364b4ba8    