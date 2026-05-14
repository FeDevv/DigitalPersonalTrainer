package org.dpt.user.client.model;

import java.time.LocalDate;

/**
 * Record immutabile per il raggruppamento dei dati sensibili del cliente.
 * -
 * Implementa il pattern <b>Parameter Object</b> per ridurre l'arità dei costruttori 
 * nella gerarchia degli utenti, incapsulando attributi di natura anagrafica e 
 * fiscale necessari per la gestione amministrativa.
 * 
 * @param fiscalCode Codice Fiscale univoco del cliente.
 * @param address Indirizzo di residenza.
 * @param birthDate Data di nascita (utilizzata dal DB per verificare la soglia minima d'età).
 */
public record ClientPersonalInfo(
        String fiscalCode,
        String address,
        LocalDate birthDate
) {}
