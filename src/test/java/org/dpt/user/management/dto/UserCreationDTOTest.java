package org.dpt.user.management.dto;

import org.dpt.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per UserCreationDTO.
 * Verifica che il costruttore compatto applichi correttamente le regole di validazione.
 */
class UserCreationDTOTest {

    @Test
    @DisplayName("Creazione valida: non dovrebbe lanciare eccezioni")
    void constructor_ValidData_Success() {
        assertDoesNotThrow(() -> new UserCreationDTO("Federico", "Rossi", "federico@test.it", "password123"));
    }

    @Test
    @DisplayName("Dati mancanti: dovrebbe lanciare ValidationException per campi null o vuoti")
    void constructor_MissingData_ThrowsException() {
        // Nome mancante
        assertThrows(ValidationException.class, () -> new UserCreationDTO(null, "Rossi", "email@test.it", "pass"));
        assertThrows(ValidationException.class, () -> new UserCreationDTO("", "Rossi", "email@test.it", "pass"));

        // Cognome mancante
        assertThrows(ValidationException.class, () -> new UserCreationDTO("Federico", null, "email@test.it", "pass"));
        
        // Password mancante
        assertThrows(ValidationException.class, () -> new UserCreationDTO("Federico", "Rossi", "email@test.it", null));
    }

    @Test
    @DisplayName("Email malformata: dovrebbe lanciare ValidationException delegando a ValidationUtils")
    void constructor_InvalidEmail_ThrowsException() {
        assertThrows(ValidationException.class, () -> new UserCreationDTO("Federico", "Rossi", "email-non-valida", "pass"));
    }
}
