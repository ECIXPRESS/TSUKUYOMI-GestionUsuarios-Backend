package edu.dosw.application.services.SellerServices;

import edu.dosw.domain.model.Seller;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.ports.SellerRepositoryPort;
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
@DisplayName("Tests para DeleteSellerService")
class DeleteSellerServiceTest {

    @Mock
    private SellerRepositoryPort sellerRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private DeleteSellerService deleteSellerService;

    private UserId userId;
    private Seller existingSeller;

    @BeforeEach
    void setUp() {
        userId = new UserId("test-seller-id");

        existingSeller = new Seller(
                userId,
                new IdentityDocument("123456789"),
                new Email("seller@company.com"),
                new FullName("Seller Test"),
                new PasswordHash("encodedPassword"),
                "Test Company",
                "Calle 123 #45-67"
        );
    }

    @Test
    @DisplayName("Debe eliminar un seller exitosamente")
    void shouldDeleteSellerSuccessfully() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        doNothing().when(sellerRepository).delete(existingSeller);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        assertDoesNotThrow(() -> deleteSellerService.deleteSeller(userId));

        // Then
        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerRepository, times(1)).delete(existingSeller);
        verify(userRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el seller no existe")
    void shouldThrowExceptionWhenSellerNotFound() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteSellerService.deleteSeller(userId)
        );

        assertTrue(exception.getMessage().contains("Seller not found"));

        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerRepository, never()).delete(any(Seller.class));
        verify(userRepository, never()).deleteByUserId(any(UserId.class));
    }

    @Test
    @DisplayName("Debe eliminar de sellerRepository antes de userRepository")
    void shouldDeleteFromSellerRepositoryBeforeUserRepository() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        doNothing().when(sellerRepository).delete(existingSeller);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteSellerService.deleteSeller(userId);

        // Then
        var inOrder = inOrder(sellerRepository, userRepository);
        inOrder.verify(sellerRepository).delete(existingSeller);
        inOrder.verify(userRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Debe buscar el seller antes de eliminar")
    void shouldFindSellerBeforeDeleting() {
        // Given
        when(sellerRepository.findByUserId(userId)).thenReturn(Optional.of(existingSeller));
        doNothing().when(sellerRepository).delete(existingSeller);
        doNothing().when(userRepository).deleteByUserId(userId);

        // When
        deleteSellerService.deleteSeller(userId);

        // Then
        verify(sellerRepository, times(1)).findByUserId(userId);
        verify(sellerRepository, times(1)).delete(existingSeller);
    }
}
