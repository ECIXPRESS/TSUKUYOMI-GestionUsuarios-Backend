package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para PasswordHash")
class PasswordHashTest {

    @Test
    @DisplayName("Debe crear un hash de password válido")
    void shouldCreateValidPasswordHash() {
        // Given
        String validHash = "hashedPassword123";

        // When
        PasswordHash passwordHash = new PasswordHash(validHash);

        // Then
        assertNotNull(passwordHash);
        assertEquals(validHash, passwordHash.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el hash es nulo")
    void shouldThrowExceptionWhenHashIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PasswordHash(null)
        );

        assertEquals("Password hash cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debe lanzar excepción cuando el hash está vacío o solo tiene espacios")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldThrowExceptionWhenHashIsEmptyOrWhitespace(String invalidHash) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PasswordHash(invalidHash)
        );

        assertEquals("Password hash cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar el valor del hash correctamente")
    void shouldReturnHashValueCorrectly() {
        // Given
        String hashValue = "$2a$10$abcdefghijklmnopqrstuv";
        PasswordHash passwordHash = new PasswordHash(hashValue);

        // When
        String result = passwordHash.value();

        // Then
        assertEquals(hashValue, result);
    }
}
