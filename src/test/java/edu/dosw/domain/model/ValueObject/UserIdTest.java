package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para UserId")
class UserIdTest {

    @Test
    @DisplayName("Debe crear un UserId válido")
    void shouldCreateValidUserId() {
        // Given
        String validId = "user-123";

        // When
        UserId userId = new UserId(validId);

        // Then
        assertNotNull(userId);
        assertEquals(validId, userId.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el ID es nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserId(null)
        );

        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debe lanzar excepción cuando el ID está vacío o solo tiene espacios")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldThrowExceptionWhenIdIsEmptyOrWhitespace(String invalidId) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new UserId(invalidId)
        );

        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar el valor del ID correctamente")
    void shouldReturnIdValueCorrectly() {
        // Given
        String idValue = "user-456";
        UserId userId = new UserId(idValue);

        // When
        String result = userId.value();

        // Then
        assertEquals(idValue, result);
    }
}
