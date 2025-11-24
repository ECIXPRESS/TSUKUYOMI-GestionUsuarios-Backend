// VerificationCode.java
package edu.dosw.domain.model.ValueObject;

import java.time.LocalDateTime;

public record VerificationCode(
        String code,
        LocalDateTime expiresAt,
        String email,
        boolean used
) {
    public VerificationCode {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification code cannot be null or empty");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid(String inputCode) {
        return !isExpired() && !used && code.equals(inputCode);
    }

    public VerificationCode markAsUsed() {
        return new VerificationCode(code, expiresAt, email, true);
    }
}