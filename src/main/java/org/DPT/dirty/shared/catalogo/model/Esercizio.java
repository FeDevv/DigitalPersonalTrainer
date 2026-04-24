package org.DPT.dirty.shared.catalogo.model;

/**
 * Rappresenta un esercizio nel catalogo della palestra.
 * Mappa la tabella ESERCIZIO del database.
 *
 * @param codice        Codice univoco dell'esercizio.
 * @param nome          Nome dell'esercizio (es. "Push Up").
 * @param descrizione   Istruzioni o dettagli sull'esecuzione.
 * @param corpoLibero   Indica se l'esercizio non richiede macchinari.
 * @param idMacchinario ID del macchinario associato (null se corpo libero).
 * @param attivo        Indica se l'esercizio è attualmente selezionabile dai PT.
 */
public record Esercizio(
        int codice,
        String nome,
        String descrizione,
        boolean corpoLibero,
        Integer idMacchinario,
        boolean attivo
) {
}
