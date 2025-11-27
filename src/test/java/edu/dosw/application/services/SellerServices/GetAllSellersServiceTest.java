package edu.dosw.application.services.SellerServices;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para GetAllSellersService")
class GetAllSellersServiceTest {

    @Mock
    private SellerRepositoryPort sellerRepository;

    @InjectMocks
    private GetAllSellersService getAllSellersService;

    private Seller seller1;
    private Seller seller2;

    @BeforeEach
    void setUp() {
        seller1 = new Seller(
                new UserId("seller-id-1"),
                new IdentityDocument("123456789"),
                new Email("seller1@company.com"),
                new FullName("Seller One"),
                new PasswordHash("encodedPassword1"),
                "Company One",
                "Address One"
        );

        seller2 = new Seller(
                new UserId("seller-id-2"),
                new IdentityDocument("987654321"),
                new Email("seller2@company.com"),
                new FullName("Seller Two"),
                new PasswordHash("encodedPassword2"),
                "Company Two",
                "Address Two"
        );
    }

    @Test
    @DisplayName("Debe obtener todos los sellers exitosamente")
    void shouldGetAllSellersSuccessfully() {
        // Given
        List<Seller> sellers = Arrays.asList(seller1, seller2);
        when(sellerRepository.findAll()).thenReturn(sellers);

        // When
        List<Seller> result = getAllSellersService.getAllSellers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(seller1));
        assertTrue(result.contains(seller2));

        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay sellers")
    void shouldReturnEmptyListWhenNoSellers() {
        // Given
        when(sellerRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Seller> result = getAllSellersService.getAllSellers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener sellers pendientes exitosamente")
    void shouldGetPendingSellersSuccessfully() {
        // Given
        List<Seller> pendingSellers = List.of(seller1);
        when(sellerRepository.findByApproved(false)).thenReturn(pendingSellers);

        // When
        List<Seller> result = getAllSellersService.getPendingSellers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(seller1));

        verify(sellerRepository, times(1)).findByApproved(false);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay sellers pendientes")
    void shouldReturnEmptyListWhenNoPendingSellers() {
        // Given
        when(sellerRepository.findByApproved(false)).thenReturn(Collections.emptyList());

        // When
        List<Seller> result = getAllSellersService.getPendingSellers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(sellerRepository, times(1)).findByApproved(false);
    }

    @Test
    @DisplayName("Debe llamar al repositorio correctamente para obtener todos los sellers")
    void shouldCallRepositoryCorrectlyForAllSellers() {
        // Given
        List<Seller> sellers = Arrays.asList(seller1, seller2);
        when(sellerRepository.findAll()).thenReturn(sellers);

        // When
        getAllSellersService.getAllSellers();

        // Then
        verify(sellerRepository, times(1)).findAll();
        verify(sellerRepository, never()).findByApproved(anyBoolean());
    }

    @Test
    @DisplayName("Debe llamar al repositorio correctamente para obtener sellers pendientes")
    void shouldCallRepositoryCorrectlyForPendingSellers() {
        // Given
        List<Seller> pendingSellers = List.of(seller1);
        when(sellerRepository.findByApproved(false)).thenReturn(pendingSellers);

        // When
        getAllSellersService.getPendingSellers();

        // Then
        verify(sellerRepository, times(1)).findByApproved(false);
        verify(sellerRepository, never()).findAll();
    }
}
