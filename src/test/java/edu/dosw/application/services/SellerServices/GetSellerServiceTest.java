package edu.dosw.application.services.SellerServices;

import edu.dosw.application.dto.SellerDTO;
import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
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
@DisplayName("Tests para GetSellerService")
class GetSellerServiceTest {

    @Mock
    private SellerRepositoryPort sellerRepository;

    @Mock
    private SellerWebMapper sellerWebMapper;

    @InjectMocks
    private GetSellerService getSellerService;

    private UserId userId;
    private Seller seller;
    private SellerDTO sellerDTO;

    @BeforeEach
    void setUp() {
        userId = new UserId("seller-id-1");

        seller = new Seller(
                userId,
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );

        sellerDTO = new SellerDTO(
                "seller@company.com",
                "Seller Test",
                "",
                "123456789",
                "Test Company",
                "Calle 123 #45-67"
        );
    }

    @Test
    @DisplayName("Debe obtener un seller por userId exitosamente")
    void shouldGetSellerByIdSuccessfully() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
        when(sellerWebMapper.toDTO(seller)).thenReturn(sellerDTO);

        // When
        SellerDTO result = getSellerService.getSellerById(userId);

        // Then
        assertNotNull(result);
        assertEquals("seller@company.com", result.email());
        assertEquals("Seller Test", result.fullName());
        assertEquals("123456789", result.identityDocument());
        assertEquals("Test Company", result.companyName());
        assertEquals("Calle 123 #45-67", result.businessAddress());

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerWebMapper, times(1)).toDTO(seller);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el seller no existe")
    void shouldThrowExceptionWhenSellerNotFound() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getSellerService.getSellerById(userId)
        );

        assertTrue(exception.getMessage().contains("Seller not found"));

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerWebMapper, never()).toDTO(any(Seller.class));
    }

    @Test
    @DisplayName("Debe mapear correctamente el seller a DTO")
    void shouldMapSellerToDTOCorrectly() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
        when(sellerWebMapper.toDTO(seller)).thenReturn(sellerDTO);

        // When
        SellerDTO result = getSellerService.getSellerById(userId);

        // Then
        verify(sellerWebMapper, times(1)).toDTO(seller);
        assertEquals(sellerDTO, result);
    }

    @Test
    @DisplayName("Debe buscar el seller antes de mapear")
    void shouldFindSellerBeforeMapping() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(seller));
        when(sellerWebMapper.toDTO(seller)).thenReturn(sellerDTO);

        // When
        getSellerService.getSellerById(userId);

        // Then
        var inOrder = inOrder(sellerRepository, sellerWebMapper);
        inOrder.verify(sellerRepository).findByUserId(userId);
        inOrder.verify(sellerWebMapper).toDTO(seller);
    }
}
