package org.dpt.boot.model;

/**
 * Tassonomia delle modalità di interfaccia utente supportate dal sistema.
 * -
 * L'enumerazione associa a ogni modalità un identificativo numerico univoco e una 
 * descrizione testuale, facilitando la generazione dinamica dei menu di scelta 
 * durante la fase di boot e garantendo l'estensibilità per future interfacce.
 */
public enum UIMode {
    /** Interfaccia testuale interattiva tramite terminale. */
    CLI(1, "Command Line Interface (Standard)"),
    
    /** Interfaccia grafica (Predisposta ma non ancora implementata). */
    GUI(2, "Graphical User Interface (Rich Interface)");

    private final int id;
    private final String description;

    UIMode(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Risolve un identificativo numerico nella corrispondente costante UIMode.
     * 
     * @param id L'ID inserito dall'utente in fase di configurazione.
     * @return La costante UIMode associata o null se l'ID non è riconosciuto.
     */
    public static UIMode getModeFromId(int id) {
        for (UIMode mode : UIMode.values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return null;
    }
}
