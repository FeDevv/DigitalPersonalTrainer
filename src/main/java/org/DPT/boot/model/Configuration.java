package org.DPT.boot.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Rappresenta la configurazione di avvio immutabile.
 * Focalizzata esclusivamente sulla modalità di interfaccia e sulla localizzazione.
 */
public record Configuration(UIMode uiMode, Locale locale) {

    /**
     * Costruttore compatto per la validazione dei parametri obbligatori.
     */
    public Configuration {
        Objects.requireNonNull(uiMode, "La modalità UI deve essere specificata.");
        Objects.requireNonNull(locale, "Il Locale deve essere specificato.");
    }

    /**
     * Factory method per la configurazione predefinita in riga di comando.
     */
    public static Configuration defaultCLI() {
        return new Configuration(UIMode.CLI, Locale.ITALY);
    }

    /**
     * Factory method per la configurazione predefinita grafica.
     */
    public static Configuration defaultGUI() {
        return new Configuration(UIMode.GUI, Locale.ITALY);
    }
}


