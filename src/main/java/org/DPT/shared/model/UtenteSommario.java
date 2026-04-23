package org.DPT.shared.model;

/**
 * Record generico per visualizzare il riepilogo di un utente (Staff o Cliente).
 * Utilizzato per popolare le liste nelle dashboard amministrative.
 */
public record UtenteSommario(int id, String nome, String cognome, String email, boolean attivo) {
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }
}
