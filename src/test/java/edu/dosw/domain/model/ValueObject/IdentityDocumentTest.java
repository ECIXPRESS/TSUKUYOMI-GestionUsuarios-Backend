package edu.dosw.domain.model.ValueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para IdentityDocument")
class IdentityDocumentTest {

    @Test
    @DisplayName("Debe crear un documento de identidad válido")
    void shouldCreateValidIdentityDocument() {
        // Given
        String validDocument = "123456789";

        // When
        IdentityDocument document = new IdentityDocument(validDocument);

        // Then
        assertNotNull(document);
        assertEquals(validDocument, document.value());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el documento es nulo")
    void shouldThrowExceptionWhenDocumentIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new IdentityDocument(null)
        );

        assertEquals("Identity document cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debe lanzar excepción cuando el documento está vacío o solo tiene espacios")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldThrowExceptionWhenDocumentIsEmptyOrWhitespace(String invalidDocument) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new IdentityDocument(invalidDocument)
        );

        assertEquals("Identity document cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar el valor del documento correctamente")
    void shouldReturnDocumentValueCorrectly() {
        // Given
        String documentValue = "987654321";
        IdentityDocument document = new IdentityDocument(documentValue);

        // When
        String result = document.value();

        // Then
        assertEquals(documentValue, result);
    }

    @ParameterizedTest
    @DisplayName("Debe aceptar diferentes formatos de documentos")
    @ValueSource(strings = {
            "123456789",
            "ABC-123456",
            "X1234567Y",
            "12.345.678-9"
    })
    void shouldAcceptDifferentDocumentFormats(String validDocument) {
        // When & Then
        assertDoesNotThrow(() -> new IdentityDocument(validDocument));
    }
}
