package org.dpt.boot.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Rappresenta lo stato di configurazione immutabile per l'istanza corrente dell'applicazione.
 * -
 * Questo record aggrega le impostazioni fondamentali determinate durante la fase di boot.
 * L'utilizzo dei Java Records garantisce l'immutabilità dei parametri di avvio, prevenendo
 * side-effect accidentali durante l'esecuzione dei moduli.
 * 
 * @param uiMode La modalità di visualizzazione selezionata (CLI/GUI).
 * @param locale Parametro per il supporto all'internazionalizzazione (i18n). 
 *               Sebbene il sistema attuale operi principalmente in italiano, la presenza del Locale
 *               permette la scalabilità futura per la localizzazione di messaggi, formati data
 *               e valute, senza richiedere modifiche strutturali al core.
 */
public record Configuration(UIMode uiMode, Locale locale) {

    /**
     * Costruttore compatto per l'applicazione di vincoli di integrità.
     * Implementa la Fail-Fast validation per impedire l'avvio con configurazioni parziali.
     * 
     * @throws NullPointerException Se uno dei parametri è nullo.
     */
    public Configuration {
        Objects.requireNonNull(uiMode, "Integrità Boot: la modalità UI è obbligatoria.");
        Objects.requireNonNull(locale, "Integrità Boot: il parametro Locale è obbligatorio.");
    }

    /**
     * Factory method per generare una configurazione standard CLI (Italy).
     * @return Configuration predefinita per terminale.
     */
    public static Configuration defaultCLI() {
        return new Configuration(UIMode.CLI, Locale.ITALY);
    }

    /**
     * Factory method per generare una configurazione standard GUI (Italy).
     * @return Configuration predefinita per interfaccia grafica.
     */
    public static Configuration defaultGUI() {
        return new Configuration(UIMode.GUI, Locale.ITALY);
    }
}


