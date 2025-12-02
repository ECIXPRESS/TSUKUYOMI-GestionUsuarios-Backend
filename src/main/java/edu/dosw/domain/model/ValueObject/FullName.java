package edu.dosw.domain.model.ValueObject;


public record FullName(String value) {
    public FullName {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
    }
}