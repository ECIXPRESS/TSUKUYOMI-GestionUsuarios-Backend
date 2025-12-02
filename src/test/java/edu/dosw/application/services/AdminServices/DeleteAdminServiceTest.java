package edu.dosw.application.services.AdminServices;

import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para DeleteAdminService")
class DeleteAdminServiceTest {

    @Mock
    private AdminRepositoryPort adminRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private DeleteAdminService deleteAdminService;

    private UserId userId;
    private Admin existingAdmin;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-user-id");

        existingAdmin = new Admin(
                userId,
                new IdentityDocument("123456789"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Test"),
                new PasswordHash("encodedPassword")
        );
    }

    @Test
    @DisplayName("Debe eliminar un admin exitosamente")
    void shouldDeleteAdminSuccessfully() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        doNothing().when(adminRepository).delete(existingAdmin);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        assertDoesNotThrow(() -> deleteAdminService.deleteAdmin(userId));

        // Then
        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminRepository, times(1)).delete(existingAdmin);
        verify(userRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el admin no existe")
    void shouldThrowExceptionWhenAdminNotFound() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteAdminService.deleteAdmin(userId)
        );

        assertTrue(exception.getMessage().contains("Admin not found"));

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminRepository, never()).delete(any(Admin.class));
        verify(userRepository, never()).deleteByUserId(any(UserId.class));
    }

    @Test
    @DisplayName("Debe eliminar de adminRepository antes de userRepository")
    void shouldDeleteFromAdminRepositoryBeforeUserRepository() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        doNothing().when(adminRepository).delete(existingAdmin);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteAdminService.deleteAdmin(userId);

        // Then
        var inOrder = inOrder(adminRepository, userRepository);
        inOrder.verify(adminRepository).delete(existingAdmin);
        inOrder.verify(userRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe buscar el admin antes de eliminar")
    void shouldFindAdminBeforeDeleting() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        doNothing().when(adminRepository).delete(existingAdmin);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteAdminService.deleteAdmin(userId);

        // Then
        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminRepository, times(1)).delete(existingAdmin);
    }
}
