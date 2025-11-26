package edu.dosw.application.dto;

public record AdminDTO(
        String identityDocument,
        String email,
        String fullName,
        String password
) {}