package edu.dosw.application.services.CustomerServices;

import edu.dosw.application.dto.CustomerDTO;
import edu.dosw.application.dto.command.CustomerCommands.CreateCustomerCommand;
import edu.dosw.domain.model.Customer;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.CustomerRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.infrastructure.web.mappers.CustomerWebMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para CreateCustomerService")
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private CustomerWebMapper customerWebMapper;

    @InjectMocks
    private CreateCustomerService createCustomerService;

    private CreateCustomerCommand validCommand;
    private CustomerDTO expectedDTO;
    private String generatedId;

    @BeforeEach
    void setUp() {
        generatedId = "generated-customer-id";

        validCommand = new CreateCustomerCommand(
                "123456789",
                "customer@example.com",
                "Customer Test",
                "password123",
                "3001234567"
        );

        expectedDTO = new CustomerDTO(
                "customer@example.com",
                "Customer Test",
                "",
                "123456789",
                "3001234567"
        );
    }

    @Test
    @DisplayName("Debe crear un customer exitosamente")
    void shouldCreateCustomerSuccessfully() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        CustomerDTO result = createCustomerService.createCustomer(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("customer@example.com", result.email());
        assertEquals("Customer Test", result.fullName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("3001234567", result.phoneNumber());
        assertEquals("", result.password());

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(idGenerator, times(1)).generateUniqueId();
        verify(passwordEncoder, times(1)).encode("password123");
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(userRepository, times(1)).save(any(Customer.class));
        verify(customerWebMapper, times(1)).toDTO(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createCustomerService.createCustomer(validCommand)
        );

        assertTrue(exception.getMessage().contains("Customer with this email already exists"));

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(idGenerator, never()).generateUniqueId();
        verify(passwordEncoder, never()).encode(anyString());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateCustomerCommand invalidCommand = new CreateCustomerCommand(
                "123456789",
                "invalid-email",
                "Customer Test",
                "password123",
                "3001234567"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createCustomerService.createCustomer(invalidCommand));

        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe codificar la contraseña antes de guardar")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        createCustomerService.createCustomer(validCommand);

        // Then
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Debe generar un ID único para el customer")
    void shouldGenerateUniqueIdForCustomer() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        createCustomerService.createCustomer(validCommand);

        // Then
        verify(idGenerator, times(1)).generateUniqueId();
    }

    @Test
    @DisplayName("Debe guardar en customerRepository antes de userRepository")
    void shouldSaveInCustomerRepositoryBeforeUserRepository() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        createCustomerService.createCustomer(validCommand);

        // Then
        var inOrder = inOrder(customerRepository, userRepository);
        inOrder.verify(customerRepository).save(any(Customer.class));
        inOrder.verify(userRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe verificar si el email existe antes de crear el customer")
    void shouldCheckEmailExistsBeforeCreating() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        createCustomerService.createCustomer(validCommand);

        // Then
        var inOrder = inOrder(userRepository, customerRepository);
        inOrder.verify(userRepository).existsByEmail(any(Email.class));
        inOrder.verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe crear customer con todos los campos del comando")
    void shouldCreateCustomerWithAllCommandFields() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        CustomerDTO result = createCustomerService.createCustomer(validCommand);

        // Then
        assertNotNull(result);
        assertEquals(validCommand.email(), result.email());
        assertEquals(validCommand.fullName(), result.fullName());
        assertEquals(validCommand.identityDocument(), result.identityDocument());
        assertEquals(validCommand.phoneNumber(), result.phoneNumber());
    }

    @Test
    @DisplayName("Debe mapear el customer guardado a DTO")
    void shouldMapSavedCustomerToDTO() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        createCustomerService.createCustomer(validCommand);

        // Then
        verify(customerWebMapper, times(1)).toDTO(createdCustomer);
    }

    @Test
    @DisplayName("Debe retornar el DTO mapeado del customer creado")
    void shouldReturnMappedDTOOfCreatedCustomer() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        CustomerDTO result = createCustomerService.createCustomer(validCommand);

        // Then
        assertEquals(expectedDTO, result);
    }

    @Test
    @DisplayName("Debe crear customer con phoneNumber correcto")
    void shouldCreateCustomerWithCorrectPhoneNumber() {
        // Given
        Customer createdCustomer = new Customer(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("customer@example.com"),
                new FullName("Customer Test"),
                new PasswordHash("encodedPassword"),
                "3001234567"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(userRepository.save(any(Customer.class))).thenReturn(createdCustomer);
        when(customerWebMapper.toDTO(any(Customer.class))).thenReturn(expectedDTO);

        // When
        CustomerDTO result = createCustomerService.createCustomer(validCommand);

        // Then
        assertEquals("3001234567", result.phoneNumber());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando identityDocument es inválido")
    void shouldThrowExceptionWhenIdentityDocumentIsInvalid() {
        // Given
        CreateCustomerCommand invalidCommand = new CreateCustomerCommand(
                "",
                "customer@example.com",
                "Customer Test",
                "password123",
                "3001234567"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createCustomerService.createCustomer(invalidCommand));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando fullName es inválido")
    void shouldThrowExceptionWhenFullNameIsInvalid() {
        // Given
        CreateCustomerCommand invalidCommand = new CreateCustomerCommand(
                "123456789",
                "customer@example.com",
                "",
                "password123",
                "3001234567"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createCustomerService.createCustomer(invalidCommand));

        verify(customerRepository, never()).save(any(Customer.class));
    }
}
