package org.dpt.user.owner.factory;

import org.dpt.boot.model.UIMode;
import org.dpt.user.owner.controller.OwnerCLIController;
import org.dpt.user.owner.controller.OwnerUI;

import java.util.Scanner;

/**
 * Factory locale per il modulo funzionale del Proprietario.
 * 
 * Implementa il pattern "Simple Factory" per la creazione dell'interfaccia utente 
 * specifica. Il metodo {@link #getUI(UIMode, Scanner)} decide quale implementazione 
 * di {@link OwnerUI} istanziare in base alla configurazione ambientale determinata 
 * in fase di boot, garantendo il totale disaccoppiamento tra la logica di business 
 * e la tecnologia di presentazione.
 */
public class OwnerUIFactory {

    /** Impedisce l'istanziazione esterna della Factory (Utility Class pattern). */
    private OwnerUIFactory() {
        throw new IllegalStateException("Utility class: non istanziabile.");
    }

    /**
     * Risolve e istanzia l'implementazione UI corretta.
     * @param mode Modalità UI desiderata (CLI/GUI).
     * @param scanner Scanner condiviso per la gestione dell'input.
     * @return L'istanza concreta di OwnerUI.
     * @throws UnsupportedOperationException Se la modalità richiesta non è ancora implementata.
     */
    public static OwnerUI getUI(UIMode mode, Scanner scanner) {
        return switch (mode) {
            case CLI -> new OwnerCLIController(scanner);
            case GUI -> throw new UnsupportedOperationException("Ingegneria: l'interfaccia grafica (GUI) per il modulo Proprietario non è attualmente disponibile.");
        };
    }
}
