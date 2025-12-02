package edu.dosw.infrastructure.persistence.mappers;

import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.infrastructure.persistence.documents.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para UserMongoMapper")
class UserMongoMapperTest {

    private UserMongoMapper mapper;
    private User user;
    private UserDocument userDocument;

    @BeforeEach
    void setUp() {
        mapper = new UserMongoMapper();

        user = new User(
                new UserId("user-123"),
                new IdentityDocument("123456789"),
                new Email("test@example.com"),
                new FullName("Test User"),
                new PasswordHash("hashedPassword123"),
                Role.CUSTOMER
        );

        userDocument = UserDocument.builder()
                .userId("user-123")
                .identityDocument("123456789")
                .email("test@example.com")
                .fullName("Test User")
                .passwordHash("hashedPassword123")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe convertir User a UserDocument correctamente")
    void shouldConvertUserToDocumentCorrectly() {
        // When
        UserDocument result = mapper.toDocument(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getUserId().value(), result.getUserId());
        assertEquals(user.getIdentityDocument().value(), result.getIdentityDocument());
        assertEquals(user.getEmail().value(), result.getEmail());
        assertEquals(user.getFullName().value(), result.getFullName());
        assertEquals(user.getPasswordHash().value(), result.getPasswordHash());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("Debe convertir UserDocument a User correctamente")
    void shouldConvertDocumentToUserCorrectly() {
        // When
        User result = mapper.toDomain(userDocument);

        // Then
        assertNotNull(result);
        assertEquals(userDocument.getUserId(), result.getUserId().value());
        assertEquals(userDocument.getIdentityDocument(), result.getIdentityDocument().value());
        assertEquals(userDocument.getEmail(), result.getEmail().value());
        assertEquals(userDocument.getFullName(), result.getFullName().value());
        assertEquals(userDocument.getPasswordHash(), result.getPasswordHash().value());
        assertEquals(userDocument.getRole(), result.getRole());
    }

    @Test
    @DisplayName("Debe retornar null cuando el documento es null")
    void shouldReturnNullWhenDocumentIsNull() {
        // When
        User result = mapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe mantener el role ADMIN correctamente")
    void shouldMaintainAdminRoleCorrectly() {
        // Given
        User adminUser = new User(
                new UserId("admin-123"),
                new IdentityDocument("987654321"),
                new Email("admin@example.com"),
                new FullName("Admin User"),
                new PasswordHash("adminHash"),
                Role.ADMIN
        );

        // When
        UserDocument result = mapper.toDocument(adminUser);

        // Then
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    @DisplayName("Debe mantener el role SELLER correctamente")
    void shouldMaintainSellerRoleCorrectly() {
        // Given
        User sellerUser = new User(
                new UserId("seller-123"),
                new IdentityDocument("111222333"),
                new Email("seller@example.com"),
                new FullName("Seller User"),
                new PasswordHash("sellerHash"),
                Role.SELLER
        );

        // When
        UserDocument result = mapper.toDocument(sellerUser);

        // Then
        assertEquals(Role.SELLER, result.getRole());
    }

    @Test
    @DisplayName("Debe preservar la fecha de creación en la conversión")
    void shouldPreserveCreatedAtInConversion() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 1, 15, 10, 30);
        UserDocument documentWithDate = UserDocument.builder()
                .userId("user-123")
                .identityDocument("123456789")
                .email("test@example.com")
                .fullName("Test User")
                .passwordHash("hashedPassword123")
                .role(Role.CUSTOMER)
                .createdAt(specificDate)
                .build();

        // When
        User result = mapper.toDomain(documentWithDate);
        UserDocument backToDocument = mapper.toDocument(result);

        // Then
        assertEquals(result.getCreatedAt(), backToDocument.getCreatedAt());
    }

    @Test
    @DisplayName("Debe manejar conversión bidireccional correctamente")
    void shouldHandleBidirectionalConversionCorrectly() {
        // When
        UserDocument document = mapper.toDocument(user);
        User reconstructedUser = mapper.toDomain(document);

        // Then
        assertEquals(user.getUserId().value(), reconstructedUser.getUserId().value());
        assertEquals(user.getEmail().value(), reconstructedUser.getEmail().value());
        assertEquals(user.getFullName().value(), reconstructedUser.getFullName().value());
        assertEquals(user.getPasswordHash().value(), reconstructedUser.getPasswordHash().value());
        assertEquals(user.getRole(), reconstructedUser.getRole());
    }

    @Test
    @DisplayName("Debe manejar emails con diferentes formatos")
    void shouldHandleDifferentEmailFormats() {
        // Given
        User userWithComplexEmail = new User(
                new UserId("user-456"),
                new IdentityDocument("999888777"),
                new Email("complex.email+tag@subdomain.example.com"),
                new FullName("Complex User"),
                new PasswordHash("complexHash"),
                Role.CUSTOMER
        );

        // When
        UserDocument result = mapper.toDocument(userWithComplexEmail);

        // Then
        assertEquals("complex.email+tag@subdomain.example.com", result.getEmail());
    }
}
