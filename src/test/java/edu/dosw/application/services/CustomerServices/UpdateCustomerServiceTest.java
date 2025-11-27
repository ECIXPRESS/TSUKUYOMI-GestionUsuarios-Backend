package edu.dosw.application.services.CustomerServices;

import edu.dosw.application.dto.CustomerUpdateDTO;
import edu.dosw.application.dto.command.CustomerCommands.UpdateCustomerCommand;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
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
@DisplayName("Tests para UpdateCustomerService")
class UpdateCustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private CustomerWebMapper customerWebMapper;

    @InjectMocks
    private UpdateCustomerService updateCustomerService;

    private UserId userId;
    private Customer existingCustomer;
    private UpdateCustomerCommand validCommand;
    private CustomerUpdateDTO expectedDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-customer-id");

        existingCustomer = new Customer(
                userId,
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Original"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        validCommand = new UpdateCustomerCommand(
                "987654321",
                "customerupdated@example.com",
                "Customer Actualizado",
                "3009876543"
        );

        expectedDTO = new CustomerUpdateDTO(
                "987654321",
                "customerupdated@example.com",
                "Customer Actualizado",
                "3009876543"
        );
    }

    @Test
    @DisplayName("Debe actualizar un customer exitosamente")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        Customer updatedCustomer = new Customer(
                userId,
                new IdentityDocument("987654321"),
                new Email("customerupdated@example.com"),
                new FullName("Customer Actualizado"),
                new PasswordHash("encodedPassword"),
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        CustomerUpdateDTO result = updateCustomerService.updateCustomer(userId, validCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("customerupdated@example.com", result.email());
        assertEquals("Customer Actualizado", result.fullName());
        assertEquals("3009876543", result.phoneNumber());

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).save(any(Customer.class));
        verify(customerWebMapper, times(1)).toUpdateDTO(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el customer no existe")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateCustomerService.updateCustomer(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(userRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateCustomerService.updateCustomer(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el documento de identidad cuando los demás campos son nulos")
    void shouldUpdateOnlyIdentityDocumentWhenOtherFieldsAreNull() {
        // Given
        UpdateCustomerCommand partialCommand = new UpdateCustomerCommand(
                "987654321",
                null,
                null,
                null
        );

        Customer partialUpdatedCustomer = new Customer(
                userId,
                new IdentityDocument("987654321"),
                new Email("customer@example.com"),
                new FullName("Customer Original"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        CustomerUpdateDTO partialDTO = new CustomerUpdateDTO(
                "987654321",
                "customer@example.com",
                "Customer Original",
                "3001234567"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(partialUpdatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(partialUpdatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(partialDTO);

        // When
        CustomerUpdateDTO result = updateCustomerService.updateCustomer(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("customer@example.com", result.email());
        assertEquals("Customer Original", result.fullName());
        assertEquals("3001234567", result.phoneNumber());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el phoneNumber cuando los demás campos son nulos")
    void shouldUpdateOnlyPhoneNumberWhenOtherFieldsAreNull() {
        // Given
        UpdateCustomerCommand partialCommand = new UpdateCustomerCommand(
                null,
                null,
                null,
                "3009876543"
        );

        Customer partialUpdatedCustomer = new Customer(
                userId,
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Original"),
                new PasswordHash("encodedPassword"),
                "3009876543"
        );

        CustomerUpdateDTO partialDTO = new CustomerUpdateDTO(
                "123456789",
                "customer@example.com",
                "Customer Original",
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(partialUpdatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(partialUpdatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(partialDTO);

        // When
        CustomerUpdateDTO result = updateCustomerService.updateCustomer(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("3009876543", result.phoneNumber());
        assertEquals("123456789", result.identityDocument());
        assertEquals("customer@example.com", result.email());
        assertEquals("Customer Original", result.fullName());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe guardar en ambos repositorios en el orden correcto")
    void shouldSaveInCorrectOrder() {
        // Given
        Customer updatedCustomer = new Customer(
                userId,
                new IdentityDocument("987654321"),
                new Email("customerupdated@example.com"),
                new FullName("Customer Actualizado"),
                new PasswordHash("encodedPassword"),
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        updateCustomerService.updateCustomer(userId, validCommand);

        // Then
        var inOrder = inOrder(customerRepository, userRepository);
        inOrder.verify(customerRepository).save(any(Customer.class));
        inOrder.verify(userRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando no se actualiza el email")
    void shouldNotCheckDuplicateEmailWhenEmailNotUpdated() {
        // Given
        UpdateCustomerCommand commandWithoutEmail = new UpdateCustomerCommand(
                "987654321",
                null,
                "Customer Actualizado",
                "3009876543"
        );

        Customer updatedCustomer = new Customer(
                userId,
                new IdentityDocument("987654321"),
                new Email("customer@example.com"),
                new FullName("Customer Actualizado"),
                new PasswordHash("encodedPassword"),
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(new CustomerUpdateDTO(
                "987654321", "customer@example.com", "Customer Actualizado", "3009876543"
        ));

        // When
        updateCustomerService.updateCustomer(userId, commandWithoutEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando el email no cambia")
    void shouldNotCheckDuplicateEmailWhenEmailUnchanged() {
        // Given
        UpdateCustomerCommand commandWithSameEmail = new UpdateCustomerCommand(
                "987654321",
                "customer@example.com",
                "Customer Actualizado",
                "3009876543"
        );

        Customer updatedCustomer = new Customer(
                userId,
                new IdentityDocument("987654321"),
                new Email("customer@example.com"),
                new FullName("Customer Actualizado"),
                new PasswordHash("encodedPassword"),
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        when(customerWebMapper.toUpdateDTO(any(Customer.class))).thenReturn(new CustomerUpdateDTO(
                "987654321", "customer@example.com", "Customer Actualizado", "3009876543"
        ));

        // When
        updateCustomerService.updateCustomer(userId, commandWithSameEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        UpdateCustomerCommand invalidCommand = new UpdateCustomerCommand(
                "987654321",
                "invalid-email",
                "Customer Actualizado",
                "3009876543"
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> updateCustomerService.updateCustomer(userId, invalidCommand));

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
