package org.DPT.shared.catalogo.model;

/**
 * Rappresenta un macchinario presente in palestra.
 * Mappa la tabella MACCHINARIO del database.
 *
 * @param id          ID univoco del macchinario.
 * @param nome        Nome del macchinario (es. "Panca Piana").
 * @param descrizione Descrizione opzionale delle funzionalità.
 * @param attivo      Stato del macchinario (disponibile o disattivato).
 */
public record Macchinario(int id, String nome, String descrizione, boolean attivo) {
}
