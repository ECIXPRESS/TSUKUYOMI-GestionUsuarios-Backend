package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.AdminDTO;
import edu.dosw.application.dto.AdminUpdateDTO;
import edu.dosw.application.dto.command.AdminCommands.CreateAdminCommand;
import edu.dosw.application.dto.command.AdminCommands.UpdateAdminCommand;
import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para AdminWebMapper")
class AdminWebMapperTest {

    private AdminWebMapper mapper;
    private Admin admin;
    private AdminDTO adminDTO;
    private AdminUpdateDTO adminUpdateDTO;

    @BeforeEach
    void setUp() {
        mapper = new AdminWebMapper();

        admin = new Admin(
                new UserId("admin-123"),
                new IdentityDocument("987654321"),
                new Email("admin@example.com"),
                new FullName("Admin User"),
                new PasswordHash("adminHash123")
        );

        adminDTO = new AdminDTO(
                "987654321",
                "admin@example.com",
                "Admin User",
                "password123"
        );

        adminUpdateDTO = new AdminUpdateDTO(
                "987654321",
                "admin@example.com",
                "Admin User Updated"
        );
    }

    @Test
    @DisplayName("Debe convertir AdminDTO a CreateAdminCommand correctamente")
    void shouldConvertAdminDTOToCreateCommandCorrectly() {
        // When
        CreateAdminCommand result = mapper.toCommand(adminDTO);

        // Then
        assertNotNull(result);
        assertEquals(adminDTO.identityDocument(), result.identityDocument());
        assertEquals(adminDTO.email(), result.email());
        assertEquals(adminDTO.fullName(), result.fullName());
        assertEquals(adminDTO.password(), result.password());
    }

    @Test
    @DisplayName("Debe convertir AdminUpdateDTO a UpdateAdminCommand correctamente")
    void shouldConvertAdminUpdateDTOToUpdateCommandCorrectly() {
        // When
        UpdateAdminCommand result = mapper.toCommand(adminUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(adminUpdateDTO.identityDocument(), result.identityDocument());
        assertEquals(adminUpdateDTO.email(), result.email());
        assertEquals(adminUpdateDTO.fullName(), result.fullName());
    }

    @Test
    @DisplayName("Debe convertir Admin a AdminDTO correctamente")
    void shouldConvertAdminToAdminDTOCorrectly() {
        // When
        AdminDTO result = mapper.toDTO(admin);

        // Then
        assertNotNull(result);
        assertEquals(admin.getEmail().value(), result.identityDocument());
        assertEquals(admin.getFullName().value(), result.email());
        assertEquals("", result.fullName());
        assertEquals(admin.getIdentityDocument().value(), result.password());
    }

    @Test
    @DisplayName("Debe convertir Admin a AdminUpdateDTO correctamente")
    void shouldConvertAdminToAdminUpdateDTOCorrectly() {
        // When
        AdminUpdateDTO result = mapper.toUpdateDTO(admin);

        // Then
        assertNotNull(result);
        assertEquals(admin.getIdentityDocument().value(), result.identityDocument());
        assertEquals(admin.getEmail().value(), result.email());
        assertEquals(admin.getFullName().value(), result.fullName());
    }

    @Test
    @DisplayName("Debe incluir password en CreateAdminCommand")
    void shouldIncludePasswordInCreateCommand() {
        // When
        CreateAdminCommand result = mapper.toCommand(adminDTO);

        // Then
        assertNotNull(result.password());
        assertEquals("password123", result.password());
    }

    @Test
    @DisplayName("UpdateAdminCommand no debe incluir password")
    void updateCommandShouldNotIncludePassword() {
        // When
        UpdateAdminCommand result = mapper.toCommand(adminUpdateDTO);

        // Then
        assertAll("Verify update command fields",
                () -> assertEquals("987654321", result.identityDocument()),
                () -> assertEquals("admin@example.com", result.email()),
                () -> assertEquals("Admin User Updated", result.fullName())
        );
    }

    @Test
    @DisplayName("Debe manejar conversión bidireccional DTO-Command-DTO")
    void shouldHandleBidirectionalDTOCommandConversion() {
        // When
        CreateAdminCommand command = mapper.toCommand(adminDTO);

        // Then
        assertEquals(adminDTO.identityDocument(), command.identityDocument());
        assertEquals(adminDTO.email(), command.email());
        assertEquals(adminDTO.fullName(), command.fullName());
        assertEquals(adminDTO.password(), command.password());
    }

    @Test
    @DisplayName("Debe preservar emails con caracteres especiales")
    void shouldPreserveEmailsWithSpecialCharacters() {
        // Given
        AdminDTO specialDTO = new AdminDTO(
                "111222333",
                "admin.special+tag@subdomain.example.com",
                "Special Admin",
                "securePass"
        );

        // When
        CreateAdminCommand result = mapper.toCommand(specialDTO);

        // Then
        assertEquals("admin.special+tag@subdomain.example.com", result.email());
    }
}
