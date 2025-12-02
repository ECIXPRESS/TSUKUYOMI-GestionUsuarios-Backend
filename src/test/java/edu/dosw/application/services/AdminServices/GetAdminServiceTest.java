package edu.dosw.application.services.AdminServices;

import edu.dosw.application.dto.AdminDTO;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.AdminRepositoryPort;
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
@DisplayName("Tests para GetAdminService")
class GetAdminServiceTest {

    @Mock
    private AdminRepositoryPort adminRepository;

    @Mock
    private AdminWebMapper adminWebMapper;

    @InjectMocks
    private GetAdminService getAdminService;

    private UserId userId;
    private Admin admin;
    private AdminDTO adminDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("admin-id-1");

        admin = new Admin(
                userId,
                new IdentityDocument("123456789"),
                new Email("admin@eci.edu.co"),
                new FullName("Admin Test"),
                new PasswordHash("encodedPassword")
        );

        // AdminDTO tiene orden: email, fullName, password, identityDocument
        adminDTO = new AdminDTO(
                "admin@eci.edu.co",
                "Admin Test",
                "",
                "123456789"
        );
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el admin no existe")
    void shouldThrowExceptionWhenAdminNotFound() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getAdminService.getAdminById(userId)
        );

        assertTrue(exception.getMessage().contains("Admin not found"));

        verify(adminRepository, times(1)).findByUserId(userId);
        verify(adminWebMapper, never()).toDTO(any(Admin.class));
    }

    @Test
    @DisplayName("Debe mapear correctamente el admin a DTO")
    void shouldMapAdminToDTOCorrectly() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(admin));
        when(adminWebMapper.toDTO(admin)).thenReturn(adminDTO);

        // When
        AdminDTO result = getAdminService.getAdminById(userId);

        // Then
        verify(adminWebMapper, times(1)).toDTO(admin);
        assertEquals(adminDTO, result);
    }

    @Test
    @DisplayName("Debe buscar el admin antes de mapear")
    void shouldFindAdminBeforeMapping() {
        // Given
        when(adminRepository.findByUserId(userId)).thenReturn(Optional.of(admin));
        when(adminWebMapper.toDTO(admin)).thenReturn(adminDTO);

        // When
        getAdminService.getAdminById(userId);

        // Then
        var inOrder = inOrder(adminRepository, adminWebMapper);
        inOrder.verify(adminRepository).findByUserId(userId);
        inOrder.verify(adminWebMapper).toDTO(admin);
    }
}
