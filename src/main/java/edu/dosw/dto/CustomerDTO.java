package edu.dosw.dto;

public record CustomerDTO(
        String email,
        String fullName,
        String password,
        String identityDocument,
        String phoneNumber
) {}