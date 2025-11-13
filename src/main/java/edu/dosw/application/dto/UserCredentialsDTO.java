package edu.dosw.application.dto;

import edu.dosw.domain.model.enums.Role;

public record UserCredentialsDTO(
        String id,
        String userId,
        String email,
        String password,
        Role role
) {}