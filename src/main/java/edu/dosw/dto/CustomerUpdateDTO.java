package edu.dosw.dto;

public record CustomerUpdateDTO(
        String identityDocument,
        String email,
        String fullName,
        String phoneNumber
) {}