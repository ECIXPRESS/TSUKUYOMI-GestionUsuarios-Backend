package edu.dosw.domain.model.ValueObject;


public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
    }
}