package edu.dosw.infrastructure.persistence.mappers;

import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import edu.dosw.infrastructure.persistence.documents.CustomerDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para CustomerMongoMapper")
class CustomerMongoMapperTest {

    private CustomerMongoMapper mapper;
    private Customer customer;
    private CustomerDocument customerDocument;

    @BeforeEach
    void setUp() {
        mapper = new CustomerMongoMapper();

        customer = new Customer(
                new UserId("customer-123"),
                new IdentityDocument("555666777"),
                new Email("customer@example.com"),
                new FullName("Customer User"),
                new PasswordHash("customerHash123"),
                "+1234567890"
        );

        customerDocument = CustomerDocument.builder()
                .userId("customer-123")
                .identityDocument("555666777")
                .email("customer@example.com")
                .fullName("Customer User")
                .passwordHash("customerHash123")
                .role(Role.CUSTOMER)
                .phoneNumber("+1234567890")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debe convertir Customer a CustomerDocument correctamente")
    void shouldConvertCustomerToDocumentCorrectly() {
        // When
        CustomerDocument result = mapper.toDocument(customer);

        // Then
        assertNotNull(result);
        assertEquals(customer.getUserId().value(), result.getUserId());
        assertEquals(customer.getIdentityDocument().value(), result.getIdentityDocument());
        assertEquals(customer.getEmail().value(), result.getEmail());
        assertEquals(customer.getFullName().value(), result.getFullName());
        assertEquals(customer.getPasswordHash().value(), result.getPasswordHash());
        assertEquals(Role.CUSTOMER, result.getRole());
        assertEquals(customer.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(customer.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("Debe convertir CustomerDocument a Customer correctamente")
    void shouldConvertDocumentToCustomerCorrectly() {
        // When
        Customer result = mapper.toDomain(customerDocument);

        // Then
        assertNotNull(result);
        assertEquals(customerDocument.getUserId(), result.getUserId().value());
        assertEquals(customerDocument.getIdentityDocument(), result.getIdentityDocument().value());
        assertEquals(customerDocument.getEmail(), result.getEmail().value());
        assertEquals(customerDocument.getFullName(), result.getFullName().value());
        assertEquals(customerDocument.getPasswordHash(), result.getPasswordHash().value());
        assertEquals(Role.CUSTOMER, result.getRole());
        assertEquals(customerDocument.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe retornar null cuando el documento es null")
    void shouldReturnNullWhenDocumentIsNull() {
        // When
        Customer result = mapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe preservar el número de teléfono correctamente")
    void shouldPreservePhoneNumberCorrectly() {
        // Given
        Customer customerWithDifferentPhone = new Customer(
                new UserId("customer-456"),
                new IdentityDocument("111222333"),
                new Email("another@example.com"),
                new FullName("Another Customer"),
                new PasswordHash("anotherHash"),
                "+9876543210"
        );

        // When
        CustomerDocument result = mapper.toDocument(customerWithDifferentPhone);

        // Then
        assertEquals("+9876543210", result.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe asignar role CUSTOMER automáticamente")
    void shouldAssignCustomerRoleAutomatically() {
        // When
        CustomerDocument result = mapper.toDocument(customer);

        // Then
        assertEquals(Role.CUSTOMER, result.getRole());
    }

    @Test
    @DisplayName("Debe manejar conversión bidireccional correctamente")
    void shouldHandleBidirectionalConversionCorrectly() {
        // When
        CustomerDocument document = mapper.toDocument(customer);
        Customer reconstructedCustomer = mapper.toDomain(document);

        // Then
        assertEquals(customer.getUserId().value(), reconstructedCustomer.getUserId().value());
        assertEquals(customer.getEmail().value(), reconstructedCustomer.getEmail().value());
        assertEquals(customer.getPhoneNumber(), reconstructedCustomer.getPhoneNumber());
    }

    @Test
    @DisplayName("Debe preservar la fecha de creación")
    void shouldPreserveCreatedAt() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 3, 10, 8, 20);
        CustomerDocument documentWithDate = CustomerDocument.builder()
                .userId("customer-789")
                .identityDocument("999888777")
                .email("dated@example.com")
                .fullName("Dated Customer")
                .passwordHash("datedHash")
                .role(Role.CUSTOMER)
                .phoneNumber("+1111111111")
                .createdAt(specificDate)
                .build();

        // When
        Customer result = mapper.toDomain(documentWithDate);
        CustomerDocument backToDocument = mapper.toDocument(result);

        // Then
        assertEquals(result.getCreatedAt(), backToDocument.getCreatedAt());
    }

    @Test
    @DisplayName("Debe manejar números de teléfono con diferentes formatos")
    void shouldHandleDifferentPhoneNumberFormats() {
        // Given
        Customer customerWithFormattedPhone = new Customer(
                new UserId("customer-999"),
                new IdentityDocument("123123123"),
                new Email("formatted@example.com"),
                new FullName("Formatted Customer"),
                new PasswordHash("formattedHash"),
                "+1 (555) 123-4567"
        );

        // When
        CustomerDocument result = mapper.toDocument(customerWithFormattedPhone);

        // Then
        assertEquals("+1 (555) 123-4567", result.getPhoneNumber());
    }
}
