package edu.dosw.application.services.UserServices;

import edu.dosw.application.dto.UserCredentialsDTO;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.UserWebMapper;
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
@DisplayName("Tests para GetUserCredentialsService")
class GetUserCredentialsServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private UserWebMapper userWebMapper;

    @InjectMocks
    private GetUserCredentialsService getUserCredentialsService;

    private Email email;
    private User user;
    private UserCredentialsDTO credentialsDTO;

    @BeforeEach
    void setUp() {
        email = new Email("user@test.com");

        user = new User(
                new UserId("user-id-1"),
                new IdentityDocument("123456789"),
                email,
                new FullName("User Test"),
                new PasswordHash("encodedPassword"),
                Role.CUSTOMER
        );

        credentialsDTO = new UserCredentialsDTO(
                "user-id-1",
                "user-id-1",
                "user@test.com",
                "encodedPassword",
                Role.CUSTOMER
        );
    }

    @Test
    @DisplayName("Debe obtener credenciales por email exitosamente")
    void shouldGetCredentialsByEmailSuccessfully() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userWebMapper.toCredentialsDTO(user)).thenReturn(credentialsDTO);

        // When
        UserCredentialsDTO result = getUserCredentialsService.getCredentialsByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals("user@test.com", result.email());
        assertEquals("user-id-1", result.userId());
        assertEquals("encodedPassword", result.password());
        assertEquals(Role.CUSTOMER, result.role());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userWebMapper, times(1)).toCredentialsDTO(user);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el usuario no existe por email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getUserCredentialsService.getCredentialsByEmail(email)
        );

        assertTrue(exception.getMessage().contains("User not found"));

        verify(userRepository, times(1)).findByEmail(email);
        verify(userWebMapper, never()).toCredentialsDTO(any(User.class));
    }

    @Test
    @DisplayName("Debe obtener usuario por email y password exitosamente")
    void shouldGetUserByEmailAndPasswordSuccessfully() {
        // Given
        String rawPassword = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);
        when(userWebMapper.toCredentialsDTO(user)).thenReturn(credentialsDTO);

        // When
        UserCredentialsDTO result = getUserCredentialsService.getUserByEmailAndPassword(email, rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("user@test.com", result.email());
        assertEquals("user-id-1", result.userId());
        assertEquals(Role.CUSTOMER, result.role());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, "encodedPassword");
        verify(userWebMapper, times(1)).toCredentialsDTO(user);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el password es incorrecto")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // Given
        String wrongPassword = "wrongPassword";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPassword, "encodedPassword")).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getUserCredentialsService.getUserByEmailAndPassword(email, wrongPassword)
        );

        assertTrue(exception.getMessage().contains("Invalid password"));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(wrongPassword, "encodedPassword");
        verify(userWebMapper, never()).toCredentialsDTO(any(User.class));
    }

    @Test
    @DisplayName("Debe verificar password antes de retornar credenciales")
    void shouldVerifyPasswordBeforeReturningCredentials() {
        // Given
        String rawPassword = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);
        when(userWebMapper.toCredentialsDTO(user)).thenReturn(credentialsDTO);

        // When
        getUserCredentialsService.getUserByEmailAndPassword(email, rawPassword);

        // Then
        var inOrder = inOrder(userRepository, passwordEncoder, userWebMapper);
        inOrder.verify(userRepository).findByEmail(email);
        inOrder.verify(passwordEncoder).matches(rawPassword, "encodedPassword");
        inOrder.verify(userWebMapper).toCredentialsDTO(user);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el usuario no existe en getUserByEmailAndPassword")
    void shouldThrowExceptionWhenUserNotFoundInGetByEmailAndPassword() {
        // Given
        String rawPassword = "password123";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getUserCredentialsService.getUserByEmailAndPassword(email, rawPassword)
        );

        assertTrue(exception.getMessage().contains("User not found"));

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userWebMapper, never()).toCredentialsDTO(any(User.class));
    }

    @Test
    @DisplayName("Debe mapear correctamente el usuario a DTO de credenciales")
    void shouldMapUserToCredentialsDTOCorrectly() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userWebMapper.toCredentialsDTO(user)).thenReturn(credentialsDTO);

        // When
        UserCredentialsDTO result = getUserCredentialsService.getCredentialsByEmail(email);

        // Then
        verify(userWebMapper, times(1)).toCredentialsDTO(user);
        assertEquals(credentialsDTO, result);
    }

    @Test
    @DisplayName("Debe retornar las credenciales mapeadas correctamente")
    void shouldReturnMappedCredentialsCorrectly() {
        // Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userWebMapper.toCredentialsDTO(user)).thenReturn(credentialsDTO);

        // When
        UserCredentialsDTO result = getUserCredentialsService.getCredentialsByEmail(email);

        // Then
        assertEquals("user-id-1", result.id());
        assertEquals("user-id-1", result.userId());
        assertEquals("user@test.com", result.email());
        assertEquals("encodedPassword", result.password());
        assertEquals(Role.CUSTOMER, result.role());
    }
}
