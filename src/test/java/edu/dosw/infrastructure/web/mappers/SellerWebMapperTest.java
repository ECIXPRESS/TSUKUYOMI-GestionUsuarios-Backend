package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.SellerDTO;
import edu.dosw.application.dto.SellerUpdateDTO;
import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para SellerWebMapper")
class SellerWebMapperTest {

    private SellerWebMapper mapper;
    private Seller seller;
    private SellerDTO sellerDTO;
    private SellerUpdateDTO sellerUpdateDTO;

    @BeforeEach
    void setUp() {
        mapper = new SellerWebMapper();

        seller = new Seller(
                new UserId("seller-123"),
                new IdentityDocument("777888999"),
                new Email("seller@example.com"),
                new FullName("Seller User"),
                new PasswordHash("sellerHash123"),
                "Tech Company Inc.",
                "123 Business St, City"
        );

        sellerDTO = new SellerDTO(
                "seller@example.com",
                "Seller User",
                "password123",
                "777888999",
                "Tech Company Inc.",
                "123 Business St, City"
        );

        sellerUpdateDTO = new SellerUpdateDTO(
                "777888999",
                "seller@example.com",
                "Seller User Updated",
                "Updated Company",
                "456 New Address"
        );
    }

    @Test
    @DisplayName("Debe convertir SellerDTO a CreateSellerCommand correctamente")
    void shouldConvertSellerDTOToCreateCommandCorrectly() {
        // When
        CreateSellerCommand result = mapper.toCommand(sellerDTO);

        // Then
        assertNotNull(result);
        assertEquals(sellerDTO.identityDocument(), result.identityDocument());
        assertEquals(sellerDTO.email(), result.email());
        assertEquals(sellerDTO.fullName(), result.fullName());
        assertEquals(sellerDTO.password(), result.password());
        assertEquals(sellerDTO.companyName(), result.companyName());
        assertEquals(sellerDTO.businessAddress(), result.businessAddress());
    }

    @Test
    @DisplayName("Debe convertir SellerUpdateDTO a UpdateSellerCommand correctamente")
    void shouldConvertSellerUpdateDTOToUpdateCommandCorrectly() {
        // When
        UpdateSellerCommand result = mapper.toCommand(sellerUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(sellerUpdateDTO.identityDocument(), result.identityDocument());
        assertEquals(sellerUpdateDTO.email(), result.email());
        assertEquals(sellerUpdateDTO.fullName(), result.fullName());
        assertEquals(sellerUpdateDTO.companyName(), result.companyName());
        assertEquals(sellerUpdateDTO.businessAddress(), result.businessAddress());
    }

    @Test
    @DisplayName("Debe convertir Seller a SellerDTO correctamente")
    void shouldConvertSellerToSellerDTOCorrectly() {
        // When
        SellerDTO result = mapper.toDTO(seller);

        // Then
        assertNotNull(result);
        assertEquals(seller.getEmail().value(), result.email());
        assertEquals(seller.getFullName().value(), result.fullName());
        assertEquals("", result.password());
        assertEquals(seller.getIdentityDocument().value(), result.identityDocument());
        assertEquals(seller.getCompanyName(), result.companyName());
        assertEquals(seller.getBusinessAddress(), result.businessAddress());
    }

    @Test
    @DisplayName("Debe convertir Seller a SellerUpdateDTO correctamente")
    void shouldConvertSellerToSellerUpdateDTOCorrectly() {
        // When
        SellerUpdateDTO result = mapper.toUpdateDTO(seller);

        // Then
        assertNotNull(result);
        assertEquals(seller.getIdentityDocument().value(), result.identityDocument());
        assertEquals(seller.getEmail().value(), result.email());
        assertEquals(seller.getFullName().value(), result.fullName());
        assertEquals(seller.getCompanyName(), result.companyName());
        assertEquals(seller.getBusinessAddress(), result.businessAddress());
    }

    @Test
    @DisplayName("Debe preservar companyName y businessAddress en todas las conversiones")
    void shouldPreserveCompanyNameAndAddressInAllConversions() {
        // When
        CreateSellerCommand createCommand = mapper.toCommand(sellerDTO);
        UpdateSellerCommand updateCommand = mapper.toCommand(sellerUpdateDTO);
        SellerDTO dto = mapper.toDTO(seller);
        SellerUpdateDTO updateDTO = mapper.toUpdateDTO(seller);

        // Then
        assertAll("Verify company name and address preservation",
                () -> assertEquals("Tech Company Inc.", createCommand.companyName()),
                () -> assertEquals("123 Business St, City", createCommand.businessAddress()),
                () -> assertEquals("Updated Company", updateCommand.companyName()),
                () -> assertEquals("456 New Address", updateCommand.businessAddress()),
                () -> assertEquals("Tech Company Inc.", dto.companyName()),
                () -> assertEquals("123 Business St, City", dto.businessAddress()),
                () -> assertEquals("Tech Company Inc.", updateDTO.companyName()),
                () -> assertEquals("123 Business St, City", updateDTO.businessAddress())
        );
    }

    @Test
    @DisplayName("toDTO debe retornar password vacío")
    void toDTOShouldReturnEmptyPassword() {
        // When
        SellerDTO result = mapper.toDTO(seller);

        // Then
        assertEquals("", result.password());
    }

    @Test
    @DisplayName("Debe incluir password en CreateSellerCommand")
    void shouldIncludePasswordInCreateCommand() {
        // When
        CreateSellerCommand result = mapper.toCommand(sellerDTO);

        // Then
        assertNotNull(result.password());
        assertEquals("password123", result.password());
    }

    @Test
    @DisplayName("Debe manejar direcciones de negocio largas")
    void shouldHandleLongBusinessAddresses() {
        // Given
        SellerDTO longAddressDTO = new SellerDTO(
                "long@example.com",
                "Long Address Seller",
                "pass",
                "999999999",
                "Global Enterprise",
                "789 Industrial Park, Building 5, Suite 300, Floor 12, Metropolitan Area, Country"
        );

        // When
        CreateSellerCommand result = mapper.toCommand(longAddressDTO);

        // Then
        assertEquals("789 Industrial Park, Building 5, Suite 300, Floor 12, Metropolitan Area, Country",
                result.businessAddress());
    }
}
