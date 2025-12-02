package edu.dosw.application.services.CustomerServices;

import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
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
@DisplayName("Tests para GetCustomerService")
class GetCustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private CustomerWebMapper customerWebMapper;

    @InjectMocks
    private GetCustomerService getCustomerService;

    private UserId userId;
    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("customer-id-1");

        customer = new Customer(
                userId,
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        customerDTO = new CustomerDTO(
                "customer@example.com",
                "Customer Test",
                "",
                "123456789",
                "3001234567"
        );
    }

    @Test
    @DisplayName("Debe obtener un customer por userId exitosamente")
    void shouldGetCustomerByIdSuccessfully() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(customerWebMapper.toDTO(customer)).thenReturn(customerDTO);

        // When
        CustomerDTO result = getCustomerService.getCustomerById(userId);

        // Then
        assertNotNull(result);
        assertEquals("customer@example.com", result.email());
        assertEquals("Customer Test", result.fullName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("3001234567", result.phoneNumber());

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerWebMapper, times(1)).toDTO(customer);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el customer no existe")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCustomerService.getCustomerById(userId)
        );

        assertTrue(exception.getMessage().contains("Customer not found"));

        verify(customerRepository, times(1)).findByUserId(userId);
        verify(customerWebMapper, never()).toDTO(any(Customer.class));
    }

    @Test
    @DisplayName("Debe mapear correctamente el customer a DTO")
    void shouldMapCustomerToDTOCorrectly() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(customerWebMapper.toDTO(customer)).thenReturn(customerDTO);

        // When
        CustomerDTO result = getCustomerService.getCustomerById(userId);

        // Then
        verify(customerWebMapper, times(1)).toDTO(customer);
        assertEquals(customerDTO, result);
    }

    @Test
    @DisplayName("Debe buscar el customer antes de mapear")
    void shouldFindCustomerBeforeMapping() {
        // Given
        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));
        when(customerWebMapper.toDTO(customer)).thenReturn(customerDTO);

        // When
        getCustomerService.getCustomerById(userId);

        // Then
        var inOrder = inOrder(customerRepository, customerWebMapper);
        inOrder.verify(customerRepository).findByUserId(userId);
        inOrder.verify(customerWebMapper).toDTO(customer);
    }
}
