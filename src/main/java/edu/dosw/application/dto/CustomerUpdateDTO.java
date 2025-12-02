package edu.dosw.application.dto;

public record CustomerUpdateDTO(
        String identityDocument,
        String email,
        String fullName,
        String phoneNumber
) {}