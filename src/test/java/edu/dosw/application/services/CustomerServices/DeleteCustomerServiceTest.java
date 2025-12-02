package edu.dosw.application.services.CustomerServices;

import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
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
@DisplayName("Tests para DeleteCustomerService")
class DeleteCustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private DeleteCustomerService deleteCustomerService;

    private UserId userId;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-customer-id");

        existingCustomer = new Customer(
                userId,
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );
    }

    @Test
    @DisplayName("Debe eliminar un customer exitosamente")
    void shouldDeleteCustomerSuccessfully() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).delete(existingCustomer);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        assertDoesNotThrow(() -> deleteCustomerService.deleteCustomer(userId));

        // Then
        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerRepository, times(1)).delete(existingCustomer);
        verify(userRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el customer no existe")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteCustomerService.deleteCustomer(userId)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerRepository, never()).delete(any(Customer.class));
        verify(userRepository, never()).deleteByUserId(any(UserId.class));
    }

    @Test
    @DisplayName("Debe eliminar de customerRepository antes de userRepository")
    void shouldDeleteFromCustomerRepositoryBeforeUserRepository() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).delete(existingCustomer);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteCustomerService.deleteCustomer(userId);

        // Then
        var inOrder = inOrder(customerRepository, userRepository);
        inOrder.verify(customerRepository).delete(existingCustomer);
        inOrder.verify(userRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe buscar el customer antes de eliminar")
    void shouldFindCustomerBeforeDeleting() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(existingCustomer));
        doNothing().when(customerRepository).delete(existingCustomer);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteCustomerService.deleteCustomer(userId);

        // Then
        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerRepository, times(1)).delete(existingCustomer);
    }
}
