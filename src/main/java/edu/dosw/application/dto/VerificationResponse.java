package edu.dosw.application.dto;

public record VerificationResponse(
        boolean success,
        String message
) {}