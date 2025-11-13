package edu.dosw.domain.model.ValueObject;

public record IdentityDocument(String value) {
    public IdentityDocument {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Identity document cannot be null or empty");
        }
    }
}