package edu.dosw.application.services.AdminServices;

import edu.dosw.application.dto.AdminDTO;
import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
import edu.dosw.utils.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CreateAdminService")
class CreateAdminServiceTest {

    @Mock
    private AdminRepositoryPort adminRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private AdminWebMapper adminWebMapper;

    @InjectMocks
    private CreateAdminService createAdminService;

    private CreateAdminCommand validCommand;
    private Admin savedAdmin;
    private AdminDTO expectedDTO;

    @BeforeEach
    void setUp() {
        validCommand = new CreateAdminCommand(
                "123456789",
                "admin@eci.edu.co",
                "Admin Test",
                "SecurePass123"
        );

        savedAdmin = new Admin(
                new UserId("test-user-id"),
                new IdentityDocument("123456789"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Test"),
                new PasswordHash("encodedPassword")
        );

        expectedDTO = new AdminDTO(
                "123456789",
                "admin@eci.edu.co",
                "Admin Test",
                "encodedPassword"
        );
    }

    @Test
    @DisplayName("Debe crear un admin exitosamente")
    void shouldCreateAdminSuccessfully() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn("test-user-id");
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(adminWebMapper.toDTO(any(Admin.class))).thenReturn(expectedDTO);

        // When
        AdminDTO result = createAdminService.createAdmin(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("123456789", result.identityDocument());
        assertEquals("admin@eci.edu.co", result.email());
        assertEquals("Admin Test", result.fullName());
        assertEquals("encodedPassword", result.password());

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(idGenerator, times(1)).generateUniqueId();
        verify(passwordEncoder, times(1)).encode("SecurePass123");
        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(userRepository, times(1)).save(any(Admin.class));
        verify(adminWebMapper, times(1)).toDTO(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createAdminService.createAdmin(validCommand)
        );

        assertTrue(exception.getMessage().contains("Admin with this email already exists"));

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(adminRepository, never()).save(any(Admin.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                null,
                "Admin Test",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                "invalid-email",
                "Admin Test",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email está vacío")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                "",
                "Admin Test",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la contraseña es nula")
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                "admin@eci.edu.co",
                "Admin Test",
                null
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(passwordEncoder, never()).encode(anyString());
        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre completo es nulo")
    void shouldThrowExceptionWhenFullNameIsNull() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                "admin@eci.edu.co",
                null,
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre completo está vacío")
    void shouldThrowExceptionWhenFullNameIsEmpty() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "123456789",
                "admin@eci.edu.co",
                "",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el documento de identidad es nulo")
    void shouldThrowExceptionWhenIdentityDocumentIsNull() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                null,
                "admin@eci.edu.co",
                "Admin Test",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el documento de identidad está vacío")
    void shouldThrowExceptionWhenIdentityDocumentIsEmpty() {
        // Given
        CreateAdminCommand invalidCommand = new CreateAdminCommand(
                "",
                "admin@eci.edu.co",
                "Admin Test",
                "SecurePass123"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createAdminService.createAdmin(invalidCommand));

        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe codificar la contraseña correctamente antes de guardar")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn("test-user-id");
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(adminWebMapper.toDTO(any(Admin.class))).thenReturn(expectedDTO);

        // When
        createAdminService.createAdmin(validCommand);

        // Then
        verify(passwordEncoder, times(1)).encode("SecurePass123");
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe guardar primero en adminRepository y luego en userRepository")
    void shouldSaveInCorrectOrder() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn("test-user-id");
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(adminWebMapper.toDTO(any(Admin.class))).thenReturn(expectedDTO);

        // When
        createAdminService.createAdmin(validCommand);

        // Then
        var inOrder = inOrder(adminRepository, userRepository);
        inOrder.verify(adminRepository).save(any(Admin.class));
        inOrder.verify(userRepository).save(any(Admin.class));
    }
}
