package org.dpt.domain.catalog.exercise.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per ExerciseCreationDTO.
 * Verifica la corretta instanziazione del record.
 */
class ExerciseCreationDTOTest {

    @Test
    @DisplayName("Istanziazione base: verifica persistenza dei dati")
    void constructor_ValidData_StoresValues() {
        ExerciseCreationDTO dto = new ExerciseCreationDTO("Panca Piana", "Eseguire 3 serie", false, 1);
        
        assertEquals("Panca Piana", dto.name());
        assertEquals("Eseguire 3 serie", dto.description());
        assertFalse(dto.isBodyweight());
        assertEquals(1, dto.machineId());
    }

    @Test
    @DisplayName("Esercizio a corpo libero: machineId dovrebbe poter essere null")
    void constructor_Bodyweight_NullMachineId() {
        ExerciseCreationDTO dto = new ExerciseCreationDTO("Push Up", "Flessioni", true, null);
        
        assertTrue(dto.isBodyweight());
        assertNull(dto.machineId());
    }
}
