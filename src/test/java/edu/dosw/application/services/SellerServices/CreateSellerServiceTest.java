package edu.dosw.application.services.SellerServices;

import edu.dosw.application.dto.SellerDTO;
import edu.dosw.application.dto.command.SellerCommands.CreateSellerCommand;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
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
@DisplayName("Tests para CreateSellerService")
class CreateSellerServiceTest {

    @Mock
    private SellerRepositoryPort sellerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private SellerWebMapper sellerWebMapper;

    @InjectMocks
    private CreateSellerService createSellerService;

    private CreateSellerCommand validCommand;
    private SellerDTO expectedDTO;
    private String generatedId;

    @BeforeEach
    void setUp() {
        generatedId = "generated-seller-id";

        validCommand = new CreateSellerCommand(
                "123456789",
                "seller@company.com",
                "Seller Test",
                "password123",
                "Test Company",
                "Calle 123 #45-67"
        );

        expectedDTO = new SellerDTO(
                "seller@company.com",
                "Seller Test",
                "",
                "123456789",
                "Test Company",
                "Calle 123 #45-67"
        );
    }

    @Test
    @DisplayName("Debe crear un seller exitosamente")
    void shouldCreateSellerSuccessfully() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerDTO result = createSellerService.createSeller(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("seller@company.com", result.email());
        assertEquals("Seller Test", result.fullName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("Test Company", result.companyName());
        assertEquals("Calle 123 #45-67", result.businessAddress());
        assertEquals("", result.password());

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(idGenerator, times(1)).generateUniqueId();
        verify(passwordEncoder, times(1)).encode("password123");
        verify(sellerRepository, times(1)).save(any(Seller.class));
        verify(userRepository, times(1)).save(any(Seller.class));
        verify(sellerWebMapper, times(1)).toDTO(any(Seller.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createSellerService.createSeller(validCommand)
        );

        assertTrue(exception.getMessage().contains("Seller with this email already exists"));

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(idGenerator, never()).generateUniqueId();
        verify(passwordEncoder, never()).encode(anyString());
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateSellerCommand invalidCommand = new CreateSellerCommand(
                "123456789",
                "invalid-email",
                "Seller Test",
                "password123",
                "Test Company",
                "Calle 123 #45-67"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createSellerService.createSeller(invalidCommand));

        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe codificar la contraseña antes de guardar")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        createSellerService.createSeller(validCommand);

        // Then
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Debe generar un ID único para el seller")
    void shouldGenerateUniqueIdForSeller() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        createSellerService.createSeller(validCommand);

        // Then
        verify(idGenerator, times(1)).generateUniqueId();
    }

    @Test
    @DisplayName("Debe guardar en sellerRepository antes de userRepository")
    void shouldSaveInSellerRepositoryBeforeUserRepository() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        createSellerService.createSeller(validCommand);

        // Then
        var inOrder = inOrder(sellerRepository, userRepository);
        inOrder.verify(sellerRepository).save(any(Seller.class));
        inOrder.verify(userRepository).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe verificar si el email existe antes de crear el seller")
    void shouldCheckEmailExistsBeforeCreating() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        createSellerService.createSeller(validCommand);

        // Then
        var inOrder = inOrder(userRepository, sellerRepository);
        inOrder.verify(userRepository).existsByEmail(any(Email.class));
        inOrder.verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe crear seller con todos los campos del comando")
    void shouldCreateSellerWithAllCommandFields() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerDTO result = createSellerService.createSeller(validCommand);

        // Then
        assertNotNull(result);
        assertEquals(validCommand.email(), result.email());
        assertEquals(validCommand.fullName(), result.fullName());
        assertEquals(validCommand.identityDocument(), result.identityDocument());
        assertEquals(validCommand.companyName(), result.companyName());
        assertEquals(validCommand.businessAddress(), result.businessAddress());
    }

    @Test
    @DisplayName("Debe mapear el seller guardado a DTO")
    void shouldMapSavedSellerToDTO() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        createSellerService.createSeller(validCommand);

        // Then
        verify(sellerWebMapper, times(1)).toDTO(createdSeller);
    }

    @Test
    @DisplayName("Debe retornar el DTO mapeado del seller creado")
    void shouldReturnMappedDTOOfCreatedSeller() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerDTO result = createSellerService.createSeller(validCommand);

        // Then
        assertEquals(expectedDTO, result);
    }

    @Test
    @DisplayName("Debe crear seller con companyName correcto")
    void shouldCreateSellerWithCorrectCompanyName() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerDTO result = createSellerService.createSeller(validCommand);

        // Then
        assertEquals("Test Company", result.companyName());
    }

    @Test
    @DisplayName("Debe crear seller con businessAddress correcto")
    void shouldCreateSellerWithCorrectBusinessAddress() {
        // Given
        Seller createdSeller = new Seller(
                new UserId(generatedId),
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(idGenerator.generateUniqueId()).thenReturn(generatedId);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(createdSeller);
        when(sellerWebMapper.toDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerDTO result = createSellerService.createSeller(validCommand);

        // Then
        assertEquals("Calle 123 #45-67", result.businessAddress());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando identityDocument es inválido")
    void shouldThrowExceptionWhenIdentityDocumentIsInvalid() {
        // Given
        CreateSellerCommand invalidCommand = new CreateSellerCommand(
                "",
                "seller@company.com",
                "Seller Test",
                "password123",
                "Test Company",
                "Calle 123 #45-67"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createSellerService.createSeller(invalidCommand));

        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando fullName es inválido")
    void shouldThrowExceptionWhenFullNameIsInvalid() {
        // Given
        CreateSellerCommand invalidCommand = new CreateSellerCommand(
                "123456789",
                "seller@company.com",
                "",
                "password123",
                "Test Company",
                "Calle 123 #45-67"
        );

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> createSellerService.createSeller(invalidCommand));

        verify(sellerRepository, never()).save(any(Seller.class));
    }
}
