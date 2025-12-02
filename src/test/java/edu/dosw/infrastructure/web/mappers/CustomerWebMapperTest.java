package edu.dosw.infrastructure.web.mappers;

import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.application.dto.CustomerUpdateDTO;
import edu.dosw.application.dto.command.CustomerCommands.CreateCustomerCommand;
import edu.dosw.application.dto.command.CustomerCommands.UpdateCustomerCommand;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para CustomerWebMapper")
class CustomerWebMapperTest {

    private CustomerWebMapper mapper;
    private Customer customer;
    private CustomerDTO customerDTO;
    private CustomerUpdateDTO customerUpdateDTO;

    @BeforeEach
    void setUp() {
        mapper = new CustomerWebMapper();

        customer = new Customer(
                new UserId("customer-123"),
                new IdentityDocument("555666777"),
                new Email("customer@example.com"),
                new FullName("Customer User"),
                new PasswordHash("customerHash123"),
                "+1234567890"
        );

        customerDTO = new CustomerDTO(
                "customer@example.com",
                "Customer User",
                "password123",
                "555666777",
                "+1234567890"
        );

        customerUpdateDTO = new CustomerUpdateDTO(
                "555666777",
                "customer@example.com",
                "Customer User Updated",
                "+0987654321"
        );
    }

    @Test
    @DisplayName("Debe convertir CustomerDTO a CreateCustomerCommand correctamente")
    void shouldConvertCustomerDTOToCreateCommandCorrectly() {
        // When
        CreateCustomerCommand result = mapper.toCommand(customerDTO);

        // Then
        assertNotNull(result);
        assertEquals(customerDTO.identityDocument(), result.identityDocument());
        assertEquals(customerDTO.email(), result.email());
        assertEquals(customerDTO.fullName(), result.fullName());
        assertEquals(customerDTO.password(), result.password());
        assertEquals(customerDTO.phoneNumber(), result.phoneNumber());
    }

    @Test
    @DisplayName("Debe convertir CustomerUpdateDTO a UpdateCustomerCommand correctamente")
    void shouldConvertCustomerUpdateDTOToUpdateCommandCorrectly() {
        // When
        UpdateCustomerCommand result = mapper.toCommand(customerUpdateDTO);

        // Then
        assertNotNull(result);
        assertEquals(customerUpdateDTO.identityDocument(), result.identityDocument());
        assertEquals(customerUpdateDTO.email(), result.email());
        assertEquals(customerUpdateDTO.fullName(), result.fullName());
        assertEquals(customerUpdateDTO.phoneNumber(), result.phoneNumber());
    }

    @Test
    @DisplayName("Debe convertir Customer a CustomerDTO correctamente")
    void shouldConvertCustomerToCustomerDTOCorrectly() {
        // When
        CustomerDTO result = mapper.toDTO(customer);

        // Then
        assertNotNull(result);
        assertEquals(customer.getEmail().value(), result.email());
        assertEquals(customer.getFullName().value(), result.fullName());
        assertEquals("", result.password());
        assertEquals(customer.getIdentityDocument().value(), result.identityDocument());
        assertEquals(customer.getPhoneNumber(), result.phoneNumber());
    }

    @Test
    @DisplayName("Debe convertir Customer a CustomerUpdateDTO correctamente")
    void shouldConvertCustomerToCustomerUpdateDTOCorrectly() {
        // When
        CustomerUpdateDTO result = mapper.toUpdateDTO(customer);

        // Then
        assertNotNull(result);
        assertEquals(customer.getIdentityDocument().value(), result.identityDocument());
        assertEquals(customer.getEmail().value(), result.email());
        assertEquals(customer.getFullName().value(), result.fullName());
        assertEquals(customer.getPhoneNumber(), result.phoneNumber());
    }

    @Test
    @DisplayName("Debe preservar el número de teléfono en todas las conversiones")
    void shouldPreservePhoneNumberInAllConversions() {
        // When
        CreateCustomerCommand createCommand = mapper.toCommand(customerDTO);
        UpdateCustomerCommand updateCommand = mapper.toCommand(customerUpdateDTO);
        CustomerDTO dto = mapper.toDTO(customer);
        CustomerUpdateDTO updateDTO = mapper.toUpdateDTO(customer);

        // Then
        assertEquals("+1234567890", createCommand.phoneNumber());
        assertEquals("+0987654321", updateCommand.phoneNumber());
        assertEquals("+1234567890", dto.phoneNumber());
        assertEquals("+1234567890", updateDTO.phoneNumber());
    }

    @Test
    @DisplayName("toDTO debe retornar password vacío")
    void toDTOShouldReturnEmptyPassword() {
        // When
        CustomerDTO result = mapper.toDTO(customer);

        // Then
        assertEquals("", result.password());
    }

    @Test
    @DisplayName("Debe incluir password en CreateCustomerCommand")
    void shouldIncludePasswordInCreateCommand() {
        // When
        CreateCustomerCommand result = mapper.toCommand(customerDTO);

        // Then
        assertNotNull(result.password());
        assertEquals("password123", result.password());
    }

    @Test
    @DisplayName("Debe manejar números de teléfono con diferentes formatos")
    void shouldHandleDifferentPhoneNumberFormats() {
        // Given
        CustomerDTO specialDTO = new CustomerDTO(
                "special@example.com",
                "Special Customer",
                "pass",
                "123123123",
                "+1 (555) 123-4567"
        );

        // When
        CreateCustomerCommand result = mapper.toCommand(specialDTO);

        // Then
        assertEquals("+1 (555) 123-4567", result.phoneNumber());
    }
}
