package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para FullName")
class FullNameTest {

    @Test
    @DisplayName("Debe crear un nombre completo válido")
    void shouldCreateValidFullName() {
        // Given
        String validName = "John Doe";

        // When
        FullName fullName = new FullName(validName);

        // Then
        assertNotNull(fullName);
        assertEquals(validName, fullName.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el nombre es nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new FullName(null)
        );

        assertEquals("Full name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debe lanzar excepción cuando el nombre está vacío o solo tiene espacios")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldThrowExceptionWhenNameIsEmptyOrWhitespace(String invalidName) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new FullName(invalidName)
        );

        assertEquals("Full name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar el valor del nombre correctamente")
    void shouldReturnNameValueCorrectly() {
        // Given
        String nameValue = "Jane Smith";
        FullName fullName = new FullName(nameValue);

        // When
        String result = fullName.value();

        // Then
        assertEquals(nameValue, result);
    }

    @ParameterizedTest
    @DisplayName("Debe aceptar nombres con caracteres especiales")
    @ValueSource(strings = {
            "José María",
            "O'Brien",
            "Jean-Pierre",
            "María José García López"
    })
    void shouldAcceptNamesWithSpecialCharacters(String validName) {
        // When & Then
        assertDoesNotThrow(() -> new FullName(validName));
    }
}
