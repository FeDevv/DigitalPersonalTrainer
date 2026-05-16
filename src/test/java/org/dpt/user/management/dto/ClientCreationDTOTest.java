package org.dpt.user.management.dto;

import org.dpt.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per ClientCreationDTO.
 * Verifica i vincoli di dominio complessi (età, codice fiscale).
 */
class ClientCreationDTOTest {

    private final UserCreationDTO validUserBase = new UserCreationDTO("Mario", "Rossi", "mario@test.it", "password");

    @Test
    @DisplayName("Creazione valida: cliente maggiorenne con CF corretto")
    void constructor_ValidData_Success() {
        assertDoesNotThrow(() -> new ClientCreationDTO(
            validUserBase,
            "RSSMRA80A01H501Z", // 16 caratteri
            "Via Roma 1",
            LocalDate.now().minusYears(20)
        ));
    }

    @Test
    @DisplayName("Codice Fiscale errato: dovrebbe lanciare ValidationException se != 16 caratteri")
    void constructor_InvalidFiscalCode_ThrowsException() {
        // Troppo corto
        assertThrows(ValidationException.class, () -> new ClientCreationDTO(
            validUserBase, "SHORT123", "Via Roma 1", LocalDate.now().minusYears(20)
        ));
        
        // Troppo lungo
        assertThrows(ValidationException.class, () -> new ClientCreationDTO(
            validUserBase, "RSSMRA80A01H501Z_EXTRA", "Via Roma 1", LocalDate.now().minusYears(20)
        ));
    }

    @Test
    @DisplayName("Limite età: dovrebbe lanciare ValidationException per utenti sotto i 14 anni")
    void constructor_TooYoung_ThrowsException() {
        LocalDate today = LocalDate.now();
        
        // 13 anni (non ammesso)
        assertThrows(ValidationException.class, () -> new ClientCreationDTO(
            validUserBase, "RSSMRA80A01H501Z", "Via Roma 1", today.minusYears(13)
        ));

        // Esattamente 14 anni (ammesso)
        assertDoesNotThrow(() -> new ClientCreationDTO(
            validUserBase, "RSSMRA80A01H501Z", "Via Roma 1", today.minusYears(14)
        ));
    }

    @Test
    @DisplayName("UserBase mancante: dovrebbe lanciare ValidationException")
    void constructor_NullUserBase_ThrowsException() {
        assertThrows(ValidationException.class, () -> new ClientCreationDTO(
            null, "RSSMRA80A01H501Z", "Via Roma 1", LocalDate.now().minusYears(20)
        ));
    }
}
