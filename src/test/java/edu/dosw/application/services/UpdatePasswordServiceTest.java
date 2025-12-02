package edu.dosw.application.services;

import edu.dosw.application.dto.PasswordUpdateRequestDTO;
import edu.dosw.application.dto.command.UpdatePasswordCommand;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
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
@DisplayName("Tests para UpdatePasswordService")
class UpdatePasswordServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private UpdatePasswordService updatePasswordService;

    private UserId userId;
    private User user;
    private UpdatePasswordCommand validCommand;

    @BeforeEach
    void setUp() {
        userId = new UserId("user-id-1");

        user = new User(
                userId,
                new IdentityDocument("123456789"),
                new Email("user@test.com"),
                new FullName("User Test"),
                new PasswordHash("oldEncodedPassword"),
                Role.CUSTOMER
        );

        validCommand = new UpdatePasswordCommand(userId, "newPassword123");
    }

    @Test
    @DisplayName("Debe actualizar password exitosamente")
    void shouldUpdatePasswordSuccessfully() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        PasswordUpdateRequestDTO result = updatePasswordService.updatePassword(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("newPassword123", result.newPassword());

        verify(userRepository, times(1)).findByUserId(userId);
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updatePasswordService.updatePassword(validCommand)
        );

        assertTrue(exception.getMessage().contains("User not found"));

        verify(userRepository, times(1)).findByUserId(userId);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe encodear el password antes de guardar")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        updatePasswordService.updatePassword(validCommand);

        // Then
        verify(passwordEncoder, times(1)).encode("newPassword123");
    }

    @Test
    @DisplayName("Debe llamar a changePassword del usuario")
    void shouldCallChangePasswordOnUser() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        updatePasswordService.updatePassword(validCommand);

        // Then
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("newPassword123");
    }

    @Test
    @DisplayName("Debe encodear password antes de llamar a changePassword")
    void shouldEncodePasswordBeforeCallingChangePassword() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        updatePasswordService.updatePassword(validCommand);

        // Then
        var inOrder = inOrder(passwordEncoder, userRepository);
        inOrder.verify(passwordEncoder).encode("newPassword123");
        inOrder.verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe buscar el usuario antes de actualizar")
    void shouldFindUserBeforeUpdating() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        updatePasswordService.updatePassword(validCommand);

        // Then
        var inOrder = inOrder(userRepository, passwordEncoder);
        inOrder.verify(userRepository).findByUserId(userId);
        inOrder.verify(passwordEncoder).encode("newPassword123");
        inOrder.verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe retornar DTO con el nuevo password")
    void shouldReturnDTOWithNewPassword() {
        // Given
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        PasswordUpdateRequestDTO result = updatePasswordService.updatePassword(validCommand);

        // Then
        assertEquals("newPassword123", result.newPassword());
    }
}
