package org.DPT.users.common.utils;

/**
 * Interfaccia comune per la gestione delle strategie di utenza (Pattern Strategy).
 * Definisce le operazioni standard di visualizzazione, modifica stato e creazione
 * che ogni modulo di gestione utenze deve implementare.
 */
public interface UserTypeHandler {
    void showList();
    void toggleStatus();
    void createNew();
}
