package edu.dosw.application.services.SellerServices;

import edu.dosw.application.dto.SellerUpdateDTO;
import edu.dosw.application.dto.command.SellerCommands.UpdateSellerCommand;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.infrastructure.web.mappers.SellerWebMapper;
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
@DisplayName("Tests para UpdateSellerService")
class UpdateSellerServiceTest {

    @Mock
    private SellerRepositoryPort sellerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private SellerWebMapper sellerWebMapper;

    @InjectMocks
    private UpdateSellerService updateSellerService;

    private UserId userId;
    private Seller existingSeller;
    private UpdateSellerCommand validCommand;
    private SellerUpdateDTO expectedDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-seller-id");

        existingSeller = new Seller(
                userId,
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Original"),
                new PasswordHash("encodedPassword"),
                "Original Company",
                "Calle Original #12-34"
        );

        validCommand = new UpdateSellerCommand(
                "987654321",
                "sellerupdated@company.com",
                "Seller Actualizado",
                "Updated Company",
                "Calle Nueva #56-78"
        );

        expectedDTO = new SellerUpdateDTO(
                "987654321",
                "sellerupdated@company.com",
                "Seller Actualizado",
                "Updated Company",
                "Calle Nueva #56-78"
        );
    }

    @Test
    @DisplayName("Debe actualizar un seller exitosamente")
    void shouldUpdateSellerSuccessfully() {
        // Given
        Seller updatedSeller = new Seller(
                userId,
                new IdentityDocument("987654321"),
                new Email("sellerupdated@company.com"),
                new FullName("Seller Actualizado"),
                new PasswordHash("encodedPassword"),
                "Updated Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        SellerUpdateDTO result = updateSellerService.updateSeller(userId, validCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("sellerupdated@company.com", result.email());
        assertEquals("Seller Actualizado", result.fullName());
        assertEquals("Updated Company", result.companyName());
        assertEquals("Calle Nueva #56-78", result.businessAddress());

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(sellerRepository, times(1)).save(any(Seller.class));
        verify(userRepository, times(1)).save(any(Seller.class));
        verify(sellerWebMapper, times(1)).toUpdateDTO(any(Seller.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el seller no existe")
    void shouldThrowExceptionWhenSellerNotFound() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateSellerService.updateSeller(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Seller not found"));

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerRepository, never()).save(any(Seller.class));
        verify(userRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateSellerService.updateSeller(userId, validCommand)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Debe actualizar solo el documento de identidad cuando los demás campos son nulos")
    void shouldUpdateOnlyIdentityDocumentWhenOtherFieldsAreNull() {
        // Given
        UpdateSellerCommand partialCommand = new UpdateSellerCommand(
                "987654321",
                null,
                null,
                null,
                null
        );

        Seller partialUpdatedSeller = new Seller(
                userId,
                new IdentityDocument("987654321"),
                new Email("seller@company.com"),
                new FullName("Seller Original"),
                new PasswordHash("encodedPassword"),
                "Original Company",
                "Calle Original #12-34"
        );

        SellerUpdateDTO partialDTO = new SellerUpdateDTO(
                "987654321",
                "seller@company.com",
                "Seller Original",
                "Original Company",
                "Calle Original #12-34"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(partialDTO);

        // When
        SellerUpdateDTO result = updateSellerService.updateSeller(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("987654321", result.identityDocument());
        assertEquals("seller@company.com", result.email());
        assertEquals("Seller Original", result.fullName());
        assertEquals("Original Company", result.companyName());
        assertEquals("Calle Original #12-34", result.businessAddress());

        verify(sellerRepository, times(1)).save(any(Seller.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe actualizar solo companyName cuando los demás campos son nulos")
    void shouldUpdateOnlyCompanyNameWhenOtherFieldsAreNull() {
        // Given
        UpdateSellerCommand partialCommand = new UpdateSellerCommand(
                null,
                null,
                null,
                "Updated Company",
                null
        );

        Seller partialUpdatedSeller = new Seller(
                userId,
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Original"),
                new PasswordHash("encodedPassword"),
                "Updated Company",
                "Calle Original #12-34"
        );

        SellerUpdateDTO partialDTO = new SellerUpdateDTO(
                "123456789",
                "seller@company.com",
                "Seller Original",
                "Updated Company",
                "Calle Original #12-34"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(partialDTO);

        // When
        SellerUpdateDTO result = updateSellerService.updateSeller(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("Updated Company", result.companyName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("seller@company.com", result.email());
        assertEquals("Seller Original", result.fullName());
        assertEquals("Calle Original #12-34", result.businessAddress());

        verify(sellerRepository, times(1)).save(any(Seller.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe actualizar solo businessAddress cuando los demás campos son nulos")
    void shouldUpdateOnlyBusinessAddressWhenOtherFieldsAreNull() {
        // Given
        UpdateSellerCommand partialCommand = new UpdateSellerCommand(
                null,
                null,
                null,
                null,
                "Calle Nueva #56-78"
        );

        Seller partialUpdatedSeller = new Seller(
                userId,
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Original"),
                new PasswordHash("encodedPassword"),
                "Original Company",
                "Calle Nueva #56-78"
        );

        SellerUpdateDTO partialDTO = new SellerUpdateDTO(
                "123456789",
                "seller@company.com",
                "Seller Original",
                "Original Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(partialUpdatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(partialDTO);

        // When
        SellerUpdateDTO result = updateSellerService.updateSeller(userId, partialCommand);

        // Then
        assertNotNull(result);
        assertEquals("Calle Nueva #56-78", result.businessAddress());
        assertEquals("123456789", result.identityDocument());
        assertEquals("seller@company.com", result.email());
        assertEquals("Seller Original", result.fullName());
        assertEquals("Original Company", result.companyName());

        verify(sellerRepository, times(1)).save(any(Seller.class));
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe guardar en ambos repositorios en el orden correcto")
    void shouldSaveInCorrectOrder() {
        // Given
        Seller updatedSeller = new Seller(
                userId,
                new IdentityDocument("987654321"),
                new Email("sellerupdated@company.com"),
                new FullName("Seller Actualizado"),
                new PasswordHash("encodedPassword"),
                "Updated Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(expectedDTO);

        // When
        updateSellerService.updateSeller(userId, validCommand);

        // Then
        var inOrder = inOrder(sellerRepository, userRepository);
        inOrder.verify(sellerRepository).save(any(Seller.class));
        inOrder.verify(userRepository).save(any(Seller.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando no se actualiza el email")
    void shouldNotCheckDuplicateEmailWhenEmailNotUpdated() {
        // Given
        UpdateSellerCommand commandWithoutEmail = new UpdateSellerCommand(
                "987654321",
                null,
                "Seller Actualizado",
                "Updated Company",
                "Calle Nueva #56-78"
        );

        Seller updatedSeller = new Seller(
                userId,
                new IdentityDocument("987654321"),
                new Email("seller@company.com"),
                new FullName("Seller Actualizado"),
                new PasswordHash("encodedPassword"),
                "Updated Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(new SellerUpdateDTO(
                "987654321", "seller@company.com", "Seller Actualizado", "Updated Company", "Calle Nueva #56-78"
        ));

        // When
        updateSellerService.updateSeller(userId, commandWithoutEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("No debe verificar email duplicado cuando el email no cambia")
    void shouldNotCheckDuplicateEmailWhenEmailUnchanged() {
        // Given
        UpdateSellerCommand commandWithSameEmail = new UpdateSellerCommand(
                "987654321",
                "seller@company.com",
                "Seller Actualizado",
                "Updated Company",
                "Calle Nueva #56-78"
        );

        Seller updatedSeller = new Seller(
                userId,
                new IdentityDocument("987654321"),
                new Email("seller@company.com"),
                new FullName("Seller Actualizado"),
                new PasswordHash("encodedPassword"),
                "Updated Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(userRepository.save(any(Seller.class))).thenReturn(updatedSeller);
        when(sellerWebMapper.toUpdateDTO(any(Seller.class))).thenReturn(new SellerUpdateDTO(
                "987654321", "seller@company.com", "Seller Actualizado", "Updated Company", "Calle Nueva #56-78"
        ));

        // When
        updateSellerService.updateSeller(userId, commandWithSameEmail);

        // Then
        verify(userRepository, never()).existsByEmail(any(Email.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el email es inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        UpdateSellerCommand invalidCommand = new UpdateSellerCommand(
                "987654321",
                "invalid-email",
                "Seller Actualizado",
                "Updated Company",
                "Calle Nueva #56-78"
        );

        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> updateSellerService.updateSeller(userId, invalidCommand));

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerRepository, never()).save(any(Seller.class));
    }
}
