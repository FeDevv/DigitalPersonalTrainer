package org.dpt.shared.utils;

import org.dpt.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test unitari per ValidationUtils.
 * Verifica la correttezza della validazione sintattica delle email.
 */
class ValidationUtilsTest {

    @Test
    @DisplayName("Email valida: dovrebbe restituire la stringa originale")
    void validateEmail_ValidEmail_ReturnsEmail() {
        String validEmail = "test@example.com";
        String result = ValidationUtils.validateEmail(validEmail);
        assertEquals(validEmail, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "#@%^%#$@#$@#.com", "@example.com", "Joe Smith <email@example.com>", "email.example.com", "email@example@example.com"})
    @DisplayName("Email non valide: dovrebbero lanciare ValidationException")
    void validateEmail_InvalidEmail_ThrowsException(String invalidEmail) {
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail(invalidEmail));
    }

    @Test
    @DisplayName("Email null o vuota: dovrebbe lanciare ValidationException")
    void validateEmail_NullOrEmpty_ThrowsException() {
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail(null));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail(""));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail("   "));
    }
}
