package edu.dosw.domain.model;

import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para User")
class UserTest {

    private UserId userId;
    private IdentityDocument identityDocument;
    private Email email;
    private FullName fullName;
    private PasswordHash passwordHash;
    private Role role;

    @BeforeEach
    void setUp() {
        userId = new UserId("user-123");
        identityDocument = new IdentityDocument("123456789");
        email = new Email("user@test.com");
        fullName = new FullName("User Test");
        passwordHash = new PasswordHash("hashedPassword123");
        role = Role.CUSTOMER;
    }

    @Test
    @DisplayName("Debe crear un usuario con todos los datos válidos")
    void shouldCreateUserWithValidData() {
        // When
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);

        // Then
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals(identityDocument, user.getIdentityDocument());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(role, user.getRole());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Debe asignar la fecha de creación automáticamente")
    void shouldAssignCreatedAtAutomatically() {
        // Given
        LocalDateTime before = LocalDateTime.now();

        // When
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);

        // Then
        LocalDateTime after = LocalDateTime.now();
        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(user.getCreatedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Debe cambiar el password correctamente")
    void shouldChangePasswordCorrectly() {
        // Given
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);
        PasswordHash newPasswordHash = new PasswordHash("newHashedPassword456");

        // When
        user.changePassword(newPasswordHash);

        // Then
        assertEquals(newPasswordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Debe mantener el password anterior si no se cambia")
    void shouldKeepOldPasswordIfNotChanged() {
        // Given
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);

        // Then
        assertEquals(passwordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Debe permitir cambiar el password múltiples veces")
    void shouldAllowMultiplePasswordChanges() {
        // Given
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);
        PasswordHash firstChange = new PasswordHash("firstChange");
        PasswordHash secondChange = new PasswordHash("secondChange");

        // When
        user.changePassword(firstChange);
        assertEquals(firstChange, user.getPasswordHash());

        user.changePassword(secondChange);

        // Then
        assertEquals(secondChange, user.getPasswordHash());
    }

    @Test
    @DisplayName("Debe mantener immutables los campos excepto password")
    void shouldKeepFieldsImmutableExceptPassword() {
        // Given
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);
        UserId originalUserId = user.getUserId();
        Email originalEmail = user.getEmail();
        Role originalRole = user.getRole();

        // When
        user.changePassword(new PasswordHash("newPassword"));

        // Then
        assertEquals(originalUserId, user.getUserId());
        assertEquals(originalEmail, user.getEmail());
        assertEquals(originalRole, user.getRole());
    }

    @Test
    @DisplayName("Debe retornar el role correctamente")
    void shouldReturnCorrectRole() {
        // Given
        User customerUser = new User(userId, identityDocument, email, fullName, passwordHash, Role.CUSTOMER);
        User adminUser = new User(new UserId("admin-123"), identityDocument, email, fullName, passwordHash, Role.ADMIN);

        // Then
        assertEquals(Role.CUSTOMER, customerUser.getRole());
        assertEquals(Role.ADMIN, adminUser.getRole());
    }

    @Test
    @DisplayName("Debe mantener la fecha de creación después de cambiar password")
    void shouldKeepCreatedAtAfterPasswordChange() {
        // Given
        User user = new User(userId, identityDocument, email, fullName, passwordHash, role);
        LocalDateTime originalCreatedAt = user.getCreatedAt();

        // When
        user.changePassword(new PasswordHash("newPassword"));

        // Then
        assertEquals(originalCreatedAt, user.getCreatedAt());
    }
}
