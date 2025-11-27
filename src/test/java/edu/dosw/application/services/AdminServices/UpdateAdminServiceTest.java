package edu.dosw.application.services.AdminServices;

import edu.dosw.application.dto.AdminUpdateDTO;
import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.AdminWebMapper;
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
@DisplayName("Tests para UpdateAdminService")
class UpdateAdminServiceTest {

    @Mock
    private AdminRepositoryPort adminRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private AdminWebMapper adminWebMapper;

    @InjectMocks
    private UpdateAdminService updateAdminService;

    private UserId userId;
    private Admin existingAdmin;
    private UpdateAdminCommand validCommand;
    private AdminUpdateDTO expectedDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-user-id");

        existingAdmin = new Admin(
                userId,
                new IdentityDocument("123456789"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Original"),
                new PasswordHash("encodedPassword")
        );

        validCommand = new UpdateAdminCommand(
                "987654321",
                "adminupdated@eci.edu.co",
                "Admin Actualizado"
        );

        expectedDTO = new AdminUpdateDTO(
                "987654321",
                "adminupdated@eci.edu.co",
                "Admin Actualizado"
        );
    }

    @Test
    @DisplayName("Debe actualizar un admin exitosamente")
    void shouldUpdateAdminSuccessfully() {
        // Given
        Admin updatedAdmin = new Admin(
                userId,
                new IdentityDocument("987654321"),
                new Email("adminupdated@eci.edu.co"),
                new FullName("Admin Actualizado"),
                new PasswordHash("encodedPassword")
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(expectedDTO);

        // When
        AdminUpdateDTO result = updateAdminService.updateAdmin(userId, validCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("adminupdated@eci.edu.co", result.email());
        assertEquals("Admin Actualizado", result.fullName());

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(userRepository, times(1)).save(any(Admin.class));
        verify(adminWebMapper, times(1)).toUpdateDTO(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el admin no existe")
    void shouldThrowExceptionWhenAdminNotFound() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateAdminService.updateAdmin(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Admin not found"));

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminRepository, never()).save(any(Admin.class));
        verify(userRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateAdminService.updateAdmin(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el documento de identidad cuando los demás campos son nulos")
    void shouldUpdateOnlyIdentityDocumentWhenOtherFieldsAreNull() {
        // Given
        UpdateAdminCommand partialCommand = new UpdateAdminCommand(
                "987654321",
                null,
                null
        );

        Admin partialUpdatedAdmin = new Admin(
                userId,
                new IdentityDocument("987654321"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Original"),
                new PasswordHash("encodedPassword")
        );

        AdminUpdateDTO partialDTO = new AdminUpdateDTO(
                "987654321",
                "admin@eci.edu.co",
                "Admin Original"
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(partialDTO);

        // When
        AdminUpdateDTO result = updateAdminService.updateAdmin(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("admin@eci.edu.co", result.email());
        assertEquals("Admin Original", result.fullName());

        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el email cuando los demás campos son nulos")
    void shouldUpdateOnlyEmailWhenOtherFieldsAreNull() {
        // Given
        UpdateAdminCommand partialCommand = new UpdateAdminCommand(
                null,
                "newemail@eci.edu.co",
                null
        );

        Admin partialUpdatedAdmin = new Admin(
                userId,
                new IdentityDocument("123456789"),
                new Email("newemail@eci.edu.co"),
                new FullName("Admin Original"),
                new PasswordHash("encodedPassword")
        );

        AdminUpdateDTO partialDTO = new AdminUpdateDTO(
                "123456789",
                "newemail@eci.edu.co",
                "Admin Original"
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(partialDTO);

        // When
        AdminUpdateDTO result = updateAdminService.updateAdmin(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("newemail@eci.edu.co", result.email());
        assertEquals("123456789", result.identityDocument());
        assertEquals("Admin Original", result.fullName());

        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el nombre completo cuando los demás campos son nulos")
    void shouldUpdateOnlyFullNameWhenOtherFieldsAreNull() {
        // Given
        UpdateAdminCommand partialCommand = new UpdateAdminCommand(
                null,
                null,
                "Nuevo Nombre"
        );

        Admin partialUpdatedAdmin = new Admin(
                userId,
                new IdentityDocument("123456789"),
                new Email("admin@eci.edu.co"),
                new FullName("Nuevo Nombre"),
                new PasswordHash("encodedPassword")
        );

        AdminUpdateDTO partialDTO = new AdminUpdateDTO(
                "123456789",
                "admin@eci.edu.co",
                "Nuevo Nombre"
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(partialUpdatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(partialDTO);

        // When
        AdminUpdateDTO result = updateAdminService.updateAdmin(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("Nuevo Nombre", result.fullName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("admin@eci.edu.co", result.email());

        verify(adminRepository, times(1)).save(any(Admin.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        UpdateAdminCommand invalidCommand = new UpdateAdminCommand(
                "987654321",
                "invalid-email",
                "Admin Actualizado"
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> updateAdminService.updateAdmin(userId, invalidCommand));

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Debe guardar en ambos repositorios en el orden correcto")
    void shouldSaveInCorrectOrder() {
        // Given
        Admin updatedAdmin = new Admin(
                userId,
                new IdentityDocument("987654321"),
                new Email("adminupdated@eci.edu.co"),
                new FullName("Admin Actualizado"),
                new PasswordHash("encodedPassword")
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(expectedDTO);

        // When
        updateAdminService.updateAdmin(userId, validCommand);

        // Then
        var inOrder = inOrder(adminRepository, userRepository);
        inOrder.verify(adminRepository).save(any(Admin.class));
        inOrder.verify(userRepository).save(any(Admin.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando no se actualiza el email")
    void shouldNotCheckDuplicateEmailWhenEmailNotUpdated() {
        // Given
        UpdateAdminCommand commandWithoutEmail = new UpdateAdminCommand(
                "987654321",
                null,
                "Admin Actualizado"
        );

        Admin updatedAdmin = new Admin(
                userId,
                new IdentityDocument("987654321"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Actualizado"),
                new PasswordHash("encodedPassword")
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(new AdminUpdateDTO(
                "987654321", "admin@eci.edu.co", "Admin Actualizado"
        ));

        // When
        updateAdminService.updateAdmin(userId, commandWithoutEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando el email no cambia")
    void shouldNotCheckDuplicateEmailWhenEmailUnchanged() {
        // Given
        UpdateAdminCommand commandWithSameEmail = new UpdateAdminCommand(
                "987654321",
                "admin@eci.edu.co",  // Mismo email que el existente
                "Admin Actualizado"
        );

        Admin updatedAdmin = new Admin(
                userId,
                new IdentityDocument("987654321"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Actualizado"),
                new PasswordHash("encodedPassword")
        );

        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(userRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(adminWebMapper.toUpdateDTO(any(Admin.class))).thenReturn(new AdminUpdateDTO(
                "987654321", "admin@eci.edu.co", "Admin Actualizado"
        ));

        // When
        updateAdminService.updateAdmin(userId, commandWithSameEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }
}
