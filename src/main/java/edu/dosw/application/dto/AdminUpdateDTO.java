package edu.dosw.application.dto;

public record AdminUpdateDTO(
        String identityDocument,
        String email,
        String fullName
) {}