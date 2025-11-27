package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para Email")
class EmailTest {

    @Test
    @DisplayName("Debe crear un email válido")
    void shouldCreateValidEmail() {
        // Given
        String validEmail = "user@example.com";

        // When
        Email email = new Email(validEmail);

        // Then
        assertNotNull(email);
        assertEquals(validEmail, email.value());
    }

    @ParameterizedTest
    @DisplayName("Debe aceptar formatos de email válidos")
    @ValueSource(strings = {
            "simple@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_123@test-domain.com",
            "123@example.com"
    })
    void shouldAcceptValidEmailFormats(String validEmail) {
        // When & Then
        assertDoesNotThrow(() -> new Email(validEmail));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email es nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(null)
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debe lanzar excepción con formatos de email inválidos")
    @ValueSource(strings = {
            "",
            "   ",
            "invalidemail",
            "@example.com",
            "user@",
            "user @example.com"
    })
    void shouldThrowExceptionWithInvalidEmailFormats(String invalidEmail) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(invalidEmail)
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar el valor del email correctamente")
    void shouldReturnEmailValueCorrectly() {
        // Given
        String emailValue = "test@example.com";
        Email email = new Email(emailValue);

        // When
        String result = email.value();

        // Then
        assertEquals(emailValue, result);
    }
}
