package edu.dosw.infrastructure.persistence.mappers;

import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.infrastructure.persistence.documents.AdminDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para AdminMongoMapper")
class AdminMongoMapperTest {

    private AdminMongoMapper mapper;
    private Admin admin;
    private AdminDocument adminDocument;

    @BeforeEach
    void setUp() {
        mapper = new AdminMongoMapper();

        admin = new Admin(
                new UserId("admin-123"),
                new IdentityDocument("987654321"),
                new Email("admin@example.com"),
                new FullName("Admin User"),
                new PasswordHash("adminHash123")
        );

        adminDocument = AdminDocument.builder()
                .userId("admin-123")
                .identityDocument("987654321")
                .email("admin@example.com")
                .fullName("Admin User")
                .passwordHash("adminHash123")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe convertir Admin a AdminDocument correctamente")
    void shouldConvertAdminToDocumentCorrectly() {
        // When
        AdminDocument result = mapper.toDocument(admin);

        // Then
        assertNotNull(result);
        assertEquals(admin.getUserId().value(), result.getUserId());
        assertEquals(admin.getIdentityDocument().value(), result.getIdentityDocument());
        assertEquals(admin.getEmail().value(), result.getEmail());
        assertEquals(admin.getFullName().value(), result.getFullName());
        assertEquals(admin.getPasswordHash().value(), result.getPasswordHash());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals(admin.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("Debe convertir AdminDocument a Admin correctamente")
    void shouldConvertDocumentToAdminCorrectly() {
        // When
        Admin result = mapper.toDomain(adminDocument);

        // Then
        assertNotNull(result);
        assertEquals(adminDocument.getUserId(), result.getUserId().value());
        assertEquals(adminDocument.getIdentityDocument(), result.getIdentityDocument().value());
        assertEquals(adminDocument.getEmail(), result.getEmail().value());
        assertEquals(adminDocument.getFullName(), result.getFullName().value());
        assertEquals(adminDocument.getPasswordHash(), result.getPasswordHash().value());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    @DisplayName("Debe retornar null cuando el documento es null")
    void shouldReturnNullWhenDocumentIsNull() {
        // When
        Admin result = mapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe asignar role ADMIN automáticamente")
    void shouldAssignAdminRoleAutomatically() {
        // When
        AdminDocument result = mapper.toDocument(admin);

        // Then
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    @DisplayName("Debe preservar la fecha de creación")
    void shouldPreserveCreatedAt() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 2, 20, 15, 45);
        AdminDocument documentWithDate = AdminDocument.builder()
                .userId("admin-456")
                .identityDocument("111222333")
                .email("admin2@example.com")
                .fullName("Another Admin")
                .passwordHash("anotherHash")
                .role(Role.ADMIN)
                .createdAt(specificDate)
                .build();

        // When
        Admin result = mapper.toDomain(documentWithDate);
        AdminDocument backToDocument = mapper.toDocument(result);

        // Then
        assertEquals(result.getCreatedAt(), backToDocument.getCreatedAt());
    }

    @Test
    @DisplayName("Debe manejar conversión bidireccional correctamente")
    void shouldHandleBidirectionalConversionCorrectly() {
        // When
        AdminDocument document = mapper.toDocument(admin);
        Admin reconstructedAdmin = mapper.toDomain(document);

        // Then
        assertEquals(admin.getUserId().value(), reconstructedAdmin.getUserId().value());
        assertEquals(admin.getEmail().value(), reconstructedAdmin.getEmail().value());
        assertEquals(admin.getFullName().value(), reconstructedAdmin.getFullName().value());
        assertEquals(admin.getPasswordHash().value(), reconstructedAdmin.getPasswordHash().value());
        assertEquals(admin.getRole(), reconstructedAdmin.getRole());
    }

    @Test
    @DisplayName("Debe manejar nombres con caracteres especiales")
    void shouldHandleNamesWithSpecialCharacters() {
        // Given
        Admin adminWithSpecialName = new Admin(
                new UserId("admin-789"),
                new IdentityDocument("444555666"),
                new Email("jose.maria@example.com"),
                new FullName("José María O'Brien"),
                new PasswordHash("specialHash")
        );

        // When
        AdminDocument result = mapper.toDocument(adminWithSpecialName);

        // Then
        assertEquals("José María O'Brien", result.getFullName());
    }

    @Test
    @DisplayName("Debe mantener el role ADMIN después de conversión bidireccional")
    void shouldMaintainAdminRoleAfterBidirectionalConversion() {
        // When
        AdminDocument document = mapper.toDocument(admin);
        Admin reconstructedAdmin = mapper.toDomain(document);
        AdminDocument finalDocument = mapper.toDocument(reconstructedAdmin);

        // Then
        assertEquals(Role.ADMIN, finalDocument.getRole());
    }
}
