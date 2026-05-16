package org.dpt.domain.catalog.machine.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per MachineCreationDTO.
 * Verifica la corretta instanziazione del record.
 */
class MachineCreationDTOTest {

    @Test
    @DisplayName("Istanziazione base: verifica persistenza dei dati")
    void constructor_ValidData_StoresValues() {
        MachineCreationDTO dto = new MachineCreationDTO("Lat Machine", "Macchinario dorsali");
        
        assertEquals("Lat Machine", dto.name());
        assertEquals("Macchinario dorsali", dto.description());
    }
}
