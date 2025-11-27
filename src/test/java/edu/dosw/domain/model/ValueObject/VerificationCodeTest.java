package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para VerificationCode")
class VerificationCodeTest {

    @Test
    @DisplayName("Debe crear un código de verificación válido")
    void shouldCreateValidVerificationCode() {
        // Given
        String code = "123456";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String email = "test@example.com";

        // When
        VerificationCode verificationCode = new VerificationCode(code, expiresAt, email, false);

        // Then
        assertNotNull(verificationCode);
        assertEquals(code, verificationCode.code());
        assertEquals(expiresAt, verificationCode.expiresAt());
        assertEquals(email, verificationCode.email());
        assertFalse(verificationCode.used());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código es nulo")
    void shouldThrowExceptionWhenCodeIsNull() {
        // Given
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String email = "test@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VerificationCode(null, expiresAt, email, false)
        );

        assertEquals("Verification code cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código está vacío")
    void shouldThrowExceptionWhenCodeIsEmpty() {
        // Given
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        String email = "test@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VerificationCode("   ", expiresAt, email, false)
        );

        assertEquals("Verification code cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la fecha de expiración es nula")
    void shouldThrowExceptionWhenExpiresAtIsNull() {
        // Given
        String code = "123456";
        String email = "test@example.com";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VerificationCode(code, null, email, false)
        );

        assertEquals("Expiration date cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email es nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        String code = "123456";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VerificationCode(code, expiresAt, null, false)
        );

        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar false cuando el código no está expirado")
    void shouldReturnFalseWhenCodeIsNotExpired() {
        // Given
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        VerificationCode code = new VerificationCode("123456", expiresAt, "test@example.com", false);

        // When
        boolean isExpired = code.isExpired();

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Debe retornar true cuando el código está expirado")
    void shouldReturnTrueWhenCodeIsExpired() {
        // Given
        LocalDateTime expiresAt = LocalDateTime.now().minusMinutes(1);
        VerificationCode code = new VerificationCode("123456", expiresAt, "test@example.com", false);

        // When
        boolean isExpired = code.isExpired();

        // Then
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Debe retornar true cuando el código es válido")
    void shouldReturnTrueWhenCodeIsValid() {
        // Given
        String validCode = "123456";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        VerificationCode code = new VerificationCode(validCode, expiresAt, "test@example.com", false);

        // When
        boolean isValid = code.isValid(validCode);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando el código es incorrecto")
    void shouldReturnFalseWhenCodeIsIncorrect() {
        // Given
        VerificationCode code = new VerificationCode("123456", LocalDateTime.now().plusMinutes(15), "test@example.com", false);

        // When
        boolean isValid = code.isValid("999999");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando el código está expirado aunque sea correcto")
    void shouldReturnFalseWhenCodeIsExpiredEvenIfCorrect() {
        // Given
        String validCode = "123456";
        VerificationCode code = new VerificationCode(validCode, LocalDateTime.now().minusMinutes(1), "test@example.com", false);

        // When
        boolean isValid = code.isValid(validCode);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando el código ya fue usado")
    void shouldReturnFalseWhenCodeWasAlreadyUsed() {
        // Given
        String validCode = "123456";
        VerificationCode code = new VerificationCode(validCode, LocalDateTime.now().plusMinutes(15), "test@example.com", true);

        // When
        boolean isValid = code.isValid(validCode);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe marcar el código como usado correctamente")
    void shouldMarkCodeAsUsedCorrectly() {
        // Given
        VerificationCode code = new VerificationCode("123456", LocalDateTime.now().plusMinutes(15), "test@example.com", false);

        // When
        VerificationCode usedCode = code.markAsUsed();

        // Then
        assertTrue(usedCode.used());
        assertEquals(code.code(), usedCode.code());
        assertEquals(code.expiresAt(), usedCode.expiresAt());
        assertEquals(code.email(), usedCode.email());
    }

    @Test
    @DisplayName("Debe mantener el código original sin cambios después de marcar como usado")
    void shouldKeepOriginalCodeUnchangedAfterMarkingAsUsed() {
        // Given
        VerificationCode originalCode = new VerificationCode("123456", LocalDateTime.now().plusMinutes(15), "test@example.com", false);

        // When
        VerificationCode usedCode = originalCode.markAsUsed();

        // Then
        assertFalse(originalCode.used());
        assertTrue(usedCode.used());
    }
}
