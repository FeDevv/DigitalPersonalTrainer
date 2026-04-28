package org.DPT.users.common.dto;

/**
 * DTO per la creazione di un utente base (Staff: PT, Addetti).
 */
public record UserCreationDTO(
    String firstName,
    String lastName,
    String email,
    String password
) {}
