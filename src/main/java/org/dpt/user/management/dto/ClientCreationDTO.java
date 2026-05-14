package org.dpt.user.management.dto;

import org.dpt.exception.ValidationException;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) specializzato per la registrazione di nuovi Clienti.
 * -
 * Estende il concetto di utenza base tramite composizione, aggregando i dati di 
 * {@link UserCreationDTO} con gli attributi specifici richiesti per l'anagrafica 
 * atleti (Codice Fiscale, Indirizzo, Data di Nascita).
 * -
 * Implementa vincoli di business direttamente nel costruttore, come la verifica 
 * dell'età minima operativa (14 anni).
 * 
 * @param userBase Dati anagrafici e credenziali comuni.
 * @param fiscalCode Identificativo fiscale (16 caratteri).
 * @param address Domicilio del cliente.
 * @param birthDate Data di nascita per il calcolo dell'idoneità.
 */
public record ClientCreationDTO(
    UserCreationDTO userBase,
    String fiscalCode,
    String address,
    LocalDate birthDate
) {
    /**
     * Applica i vincoli di dominio per l'iscrizione di un nuovo cliente.
     * @throws ValidationException Se i dati sono parziali o il cliente è minorenne sotto la soglia consentita.
     */
    public ClientCreationDTO {
        if (userBase == null) throw new ValidationException("Integrità: i dati anagrafici base sono obbligatori.");
        if (fiscalCode == null || fiscalCode.length() != 16) throw new ValidationException("Validazione: il codice fiscale deve essere composto esattamente da 16 caratteri.");
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(14))) {
            throw new ValidationException("Policy Palestra: l'iscrizione è riservata ad utenti con almeno 14 anni compiuti.");
        }
    }
}
