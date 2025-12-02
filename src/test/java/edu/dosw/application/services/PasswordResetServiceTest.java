package edu.dosw.application.services;

import edu.dosw.application.ports.EventPublisherPort;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.VerificationCodeRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PasswordResetService")
class PasswordResetServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private VerificationCodeRepositoryPort verificationCodeRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Email email;
    private User user;

    @BeforeEach
    void setUp() {
        email = new Email("user@test.com");

        user = new User(
                new UserId("user-id-1"),
                new IdentityDocument("123456789"),
                email,
                new FullName("User Test"),
                new PasswordHash("oldEncodedPassword"),
                Role.CUSTOMER
        );
    }

    @Test
    @DisplayName("Debe solicitar reset de password exitosamente")
    void shouldRequestPasswordResetSuccessfully() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(any());
        // When
        assertDoesNotThrow(() -> passwordResetService.requestPasswordReset(email));

        // Then
        verify(userRepository, times(1)).findByEmail(email);
        verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
        verify(eventPublisher, times(1)).publishPasswordResetRequested(eq(email), eq(user), anyString());
    }


    @Test
    @DisplayName("No debe lanzar excepción cuando el usuario no existe en requestPasswordReset")
    void shouldNotThrowExceptionWhenUserNotFoundInRequest() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        assertDoesNotThrow(() -> passwordResetService.requestPasswordReset(email));

        // Then
        verify(userRepository, times(1)).findByEmail(email);
        verify(verificationCodeRepository, never()).save(any(VerificationCode.class));
        verify(eventPublisher, never()).publishPasswordResetRequested(any(Email.class), any(User.class), anyString());
    }

    @Test
    @DisplayName("Debe generar código de 6 dígitos")
    void shouldGenerateSixDigitCode() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ArgumentCaptor<VerificationCode> codeCaptor = ArgumentCaptor.forClass(VerificationCode.class);
        when(verificationCodeRepository.save(codeCaptor.capture())).thenReturn(any());

        // When
        passwordResetService.requestPasswordReset(email);

        // Then
        VerificationCode savedCode = codeCaptor.getValue();
        assertTrue(savedCode.code().matches("\\d{6}"));
    }


    @Test
    @DisplayName("Debe configurar expiración del código en 15 minutos")
    void shouldSetCodeExpirationTo15Minutes() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ArgumentCaptor<VerificationCode> codeCaptor = ArgumentCaptor.forClass(VerificationCode.class);
        when(verificationCodeRepository.save(codeCaptor.capture())).thenReturn(any());

        LocalDateTime before = LocalDateTime.now().plusMinutes(15).minusSeconds(5);

        // When
        passwordResetService.requestPasswordReset(email);

        // Then
        VerificationCode savedCode = codeCaptor.getValue();
        LocalDateTime after = LocalDateTime.now().plusMinutes(15).plusSeconds(5);

        assertTrue(savedCode.expiresAt().isAfter(before));
        assertTrue(savedCode.expiresAt().isBefore(after));
    }


    @Test
    @DisplayName("Debe verificar código exitosamente")
    void shouldVerifyCodeSuccessfully() {
        // Given
        String validCode = "123456";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        doNothing().when(eventPublisher).publishPasswordResetVerified(email, validCode);

        // When
        assertDoesNotThrow(() -> passwordResetService.verifyCode(email, validCode));

        // Then
        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(eventPublisher, times(1)).publishPasswordResetVerified(email, validCode);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el código no existe")
    void shouldThrowExceptionWhenCodeNotFound() {
        // Given
        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> passwordResetService.verifyCode(email, "123456")
        );

        assertTrue(exception.getMessage().contains("Verification code not found or expired"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(eventPublisher, never()).publishPasswordResetVerified(any(Email.class), anyString());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el código es inválido")
    void shouldThrowExceptionWhenCodeIsInvalid() {
        // Given
        String validCode = "123456";
        String invalidCode = "999999";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.verifyCode(email, invalidCode)
        );

        assertTrue(exception.getMessage().contains("Invalid or expired verification code"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(eventPublisher, never()).publishPasswordResetVerified(any(Email.class), anyString());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el código está expirado")
    void shouldThrowExceptionWhenCodeIsExpired() {
        // Given
        String validCode = "123456";
        VerificationCode expiredCode = new VerificationCode(
                validCode,
                LocalDateTime.now().minusMinutes(1),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(expiredCode));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.verifyCode(email, validCode)
        );

        assertTrue(exception.getMessage().contains("Invalid or expired verification code"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(eventPublisher, never()).publishPasswordResetVerified(any(Email.class), anyString());
    }

    @Test
    @DisplayName("Debe resetear password exitosamente")
    void shouldResetPasswordSuccessfully() {
        // Given
        String validCode = "123456";
        String newPassword = "newPassword123";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(verificationCode.markAsUsed());
        doNothing().when(eventPublisher).publishPasswordResetCompleted(eq(email), eq(user), eq(true));

        // When
        assertDoesNotThrow(() -> passwordResetService.resetPassword(email, validCode, newPassword));

        // Then
        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
        verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
        verify(eventPublisher, times(1)).publishPasswordResetCompleted(email, user, true);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el código no existe en reset")
    void shouldThrowExceptionWhenCodeNotFoundInReset() {
        // Given
        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> passwordResetService.resetPassword(email, "123456", "newPassword")
        );

        assertTrue(exception.getMessage().contains("Verification code not found"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).findByEmail(any(Email.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el código es inválido en reset")
    void shouldThrowExceptionWhenCodeIsInvalidInReset() {
        // Given
        String validCode = "123456";
        String invalidCode = "999999";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(email, invalidCode, "newPassword")
        );

        assertTrue(exception.getMessage().contains("Invalid or expired verification code"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).findByEmail(any(Email.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el usuario no existe en reset")
    void shouldThrowExceptionWhenUserNotFoundInReset() {
        // Given
        String validCode = "123456";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> passwordResetService.resetPassword(email, validCode, "newPassword")
        );

        assertTrue(exception.getMessage().contains("User not found"));

        verify(verificationCodeRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe marcar el código como usado después de resetear password")
    void shouldMarkCodeAsUsedAfterReset() {
        // Given
        String validCode = "123456";
        String newPassword = "newPassword123";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        ArgumentCaptor<VerificationCode> codeCaptor = ArgumentCaptor.forClass(VerificationCode.class);

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationCodeRepository.save(codeCaptor.capture())).thenReturn(verificationCode.markAsUsed());
        doNothing().when(eventPublisher).publishPasswordResetCompleted(eq(email), eq(user), eq(true));

        // When
        passwordResetService.resetPassword(email, validCode, newPassword);

        // Then
        VerificationCode savedCode = codeCaptor.getValue();
        assertTrue(savedCode.used());
    }

    @Test
    @DisplayName("Debe encodear el nuevo password antes de guardar")
    void shouldEncodeNewPasswordBeforeSaving() {
        // Given
        String validCode = "123456";
        String newPassword = "newPassword123";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(verificationCode.markAsUsed());
        doNothing().when(eventPublisher).publishPasswordResetCompleted(eq(email), eq(user), eq(true));

        // When
        passwordResetService.resetPassword(email, validCode, newPassword);

        // Then
        verify(passwordEncoder, times(1)).encode(newPassword);
    }

    @Test
    @DisplayName("Debe publicar evento de reset completado con éxito")
    void shouldPublishPasswordResetCompletedEvent() {
        // Given
        String validCode = "123456";
        String newPassword = "newPassword123";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(verificationCode.markAsUsed());
        doNothing().when(eventPublisher).publishPasswordResetCompleted(eq(email), eq(user), eq(true));

        // When
        passwordResetService.resetPassword(email, validCode, newPassword);

        // Then
        verify(eventPublisher, times(1)).publishPasswordResetCompleted(email, user, true);
    }

    @Test
    @DisplayName("Debe guardar el usuario antes de marcar el código como usado")
    void shouldSaveUserBeforeMarkingCodeAsUsed() {
        // Given
        String validCode = "123456";
        String newPassword = "newPassword123";
        VerificationCode verificationCode = new VerificationCode(
                validCode,
                LocalDateTime.now().plusMinutes(15),
                email.value(),
                false
        );

        when(verificationCodeRepository.findByEmail(email)).thenReturn(Optional.of(verificationCode));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(verificationCode.markAsUsed());
        doNothing().when(eventPublisher).publishPasswordResetCompleted(eq(email), eq(user), eq(true));

        // When
        passwordResetService.resetPassword(email, validCode, newPassword);

        // Then
        var inOrder = inOrder(userRepository, verificationCodeRepository);
        inOrder.verify(userRepository).save(user);
        inOrder.verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    @DisplayName("Debe guardar código en repositorio cuando se solicita reset")
    void shouldSaveCodeInRepositoryWhenRequestingReset() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(any());

        // When
        passwordResetService.requestPasswordReset(email);

        // Then
        verify(verificationCodeRepository, times(1)).save(any(VerificationCode.class));
    }


    @Test
    @DisplayName("Debe publicar evento después de guardar código")
    void shouldPublishEventAfterSavingCode() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(verificationCodeRepository.save(any(VerificationCode.class))).thenReturn(any());

        // When
        passwordResetService.requestPasswordReset(email);

        // Then
        var inOrder = inOrder(verificationCodeRepository, eventPublisher);
        inOrder.verify(verificationCodeRepository).save(any(VerificationCode.class));
        inOrder.verify(eventPublisher).publishPasswordResetRequested(eq(email), eq(user), anyString());
    }

}
