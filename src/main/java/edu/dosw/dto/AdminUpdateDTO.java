package edu.dosw.dto;

public record AdminUpdateDTO(
        String identityDocument,
        String email,
        String fullName
) {}