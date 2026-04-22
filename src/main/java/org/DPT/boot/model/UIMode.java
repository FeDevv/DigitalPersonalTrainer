package org.DPT.boot.model;

/**
 * Definisce le modalità di avvio con supporto a ID dinamici.
 * Questo permette di generare menu CLI in modo automatico.
 */
public enum UIMode {
    CLI(1, "Command Line Interface"),
    GUI(2, "Graphical User Interface");

    private final int id;
    private final String description;

    // Costruttore per legare l'ID e una descrizione alla costante
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
     * Cerca la modalità corrispondente a un ID.
     * @param id l'ID inserito dall'utente.
     * @return la costante UIMode o null se non trovata.
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
