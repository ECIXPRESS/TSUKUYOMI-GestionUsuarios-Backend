package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.UserCredentialsDTO;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para UserWebMapper")
class UserWebMapperTest {

    private UserWebMapper mapper;
    private User user;

    @BeforeEach
    void setUp() {
        mapper = new UserWebMapper();

        user = new User(
                new UserId("user-123"),
                new IdentityDocument("123456789"),
                new Email("test@example.com"),
                new FullName("Test User"),
                new PasswordHash("hashedPassword123"),
                Role.CUSTOMER
        );
    }

    @Test
    @DisplayName("Debe convertir User a UserCredentialsDTO correctamente")
    void shouldConvertUserToCredentialsDTOCorrectly() {
        // When
        UserCredentialsDTO result = mapper.toCredentialsDTO(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getUserId().value(), result.id());
        assertEquals(user.getUserId().value(), result.userId());
        assertEquals(user.getEmail().value(), result.email());
        assertEquals(user.getPasswordHash().value(), result.password());
        assertEquals(user.getRole(), result.role());
    }

    @Test
    @DisplayName("Debe mapear role ADMIN correctamente")
    void shouldMapAdminRoleCorrectly() {
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
        UserCredentialsDTO result = mapper.toCredentialsDTO(adminUser);

        // Then
        assertEquals(Role.ADMIN, result.role());
    }

    @Test
    @DisplayName("Debe mapear role SELLER correctamente")
    void shouldMapSellerRoleCorrectly() {
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
        UserCredentialsDTO result = mapper.toCredentialsDTO(sellerUser);

        // Then
        assertEquals(Role.SELLER, result.role());
    }

    @Test
    @DisplayName("Debe mapear role CUSTOMER correctamente")
    void shouldMapCustomerRoleCorrectly() {
        // When
        UserCredentialsDTO result = mapper.toCredentialsDTO(user);

        // Then
        assertEquals(Role.CUSTOMER, result.role());
    }

    @Test
    @DisplayName("Debe incluir el password hash en el DTO")
    void shouldIncludePasswordHashInDTO() {
        // When
        UserCredentialsDTO result = mapper.toCredentialsDTO(user);

        // Then
        assertNotNull(result.password());
        assertEquals("hashedPassword123", result.password());
    }

    @Test
    @DisplayName("Debe usar el mismo userId para id y userId en el DTO")
    void shouldUseSameUserIdForIdAndUserId() {
        // When
        UserCredentialsDTO result = mapper.toCredentialsDTO(user);

        // Then
        assertEquals(result.id(), result.userId());
        assertEquals(user.getUserId().value(), result.id());
        assertEquals(user.getUserId().value(), result.userId());
    }

    @Test
    @DisplayName("Debe manejar emails complejos correctamente")
    void shouldHandleComplexEmailsCorrectly() {
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
        UserCredentialsDTO result = mapper.toCredentialsDTO(userWithComplexEmail);

        // Then
        assertEquals("complex.email+tag@subdomain.example.com", result.email());
    }

    @Test
    @DisplayName("Debe preservar todos los campos después de la conversión")
    void shouldPreserveAllFieldsAfterConversion() {
        // When
        UserCredentialsDTO result = mapper.toCredentialsDTO(user);

        // Then
        assertAll("Verify all fields are preserved",
                () -> assertEquals(user.getUserId().value(), result.id()),
                () -> assertEquals(user.getUserId().value(), result.userId()),
                () -> assertEquals(user.getEmail().value(), result.email()),
                () -> assertEquals(user.getPasswordHash().value(), result.password()),
                () -> assertEquals(user.getRole(), result.role())
        );
    }
}
