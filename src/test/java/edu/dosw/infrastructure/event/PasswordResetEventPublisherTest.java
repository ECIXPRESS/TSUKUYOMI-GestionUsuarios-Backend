package edu.dosw.infrastructure.event;

import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.*;
import edu.dosw.domain.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PasswordResetEventPublisher")
class PasswordResetEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private PasswordResetEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<EventWrapper> eventCaptor;

    private User user;
    private Email email;
    private String verificationCode;

    @BeforeEach
    void setUp() {
        email = new Email("test@example.com");
        verificationCode = "123456";

        user = new User(
                new UserId("user-123"),
                new IdentityDocument("123456789"),
                email,
                new FullName("Test User"),
                new PasswordHash("hashedPassword"),
                Role.CUSTOMER
        );
    }

    @Test
    @DisplayName("Debe publicar evento de solicitud de reset de contraseña correctamente")
    void shouldPublishPasswordResetRequestedEventCorrectly() {
        // When
        eventPublisher.publishPasswordResetRequested(email, user, verificationCode);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        EventWrapper capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("password.reset.requested", capturedEvent.getEventType());
        assertEquals("1.0", capturedEvent.getVersion());
        assertNotNull(capturedEvent.getEventId());
        assertNotNull(capturedEvent.getTimestamp());

        PasswordResetEventPublisher.PasswordResetRequestEventData data =
                (PasswordResetEventPublisher.PasswordResetRequestEventData) capturedEvent.getData();
        assertEquals("test@example.com", data.getEmail());
        assertEquals("user-123", data.getUserId());
        assertEquals("Test User", data.getName());
        assertEquals("123456", data.getVerificationCode());
    }

    @Test
    @DisplayName("Debe publicar evento de verificación de reset de contraseña correctamente")
    void shouldPublishPasswordResetVerifiedEventCorrectly() {
        // When
        eventPublisher.publishPasswordResetVerified(email, verificationCode);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        EventWrapper capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("password.reset.verified", capturedEvent.getEventType());
        assertEquals("1.0", capturedEvent.getVersion());
        assertNotNull(capturedEvent.getEventId());
        assertNotNull(capturedEvent.getTimestamp());

        PasswordResetEventPublisher.PasswordResetVerifiedEventData data =
                (PasswordResetEventPublisher.PasswordResetVerifiedEventData) capturedEvent.getData();
        assertEquals("test@example.com", data.getEmail());
        assertEquals("123456", data.getVerificationCode());
    }

    @Test
    @DisplayName("Debe publicar evento de reset completado exitosamente")
    void shouldPublishPasswordResetCompletedEventWithSuccess() {
        // When
        eventPublisher.publishPasswordResetCompleted(email, user, true);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        EventWrapper capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("password.reset.completed", capturedEvent.getEventType());
        assertEquals("1.0", capturedEvent.getVersion());

        PasswordResetEventPublisher.PasswordResetCompletedEventData data =
                (PasswordResetEventPublisher.PasswordResetCompletedEventData) capturedEvent.getData();
        assertEquals("test@example.com", data.getEmail());
        assertEquals("user-123", data.getUserId());
        assertEquals("Test User", data.getName());
        assertTrue(data.isSuccess());
    }

    @Test
    @DisplayName("Debe publicar evento de reset completado con fallo")
    void shouldPublishPasswordResetCompletedEventWithFailure() {
        // When
        eventPublisher.publishPasswordResetCompleted(email, user, false);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        EventWrapper capturedEvent = eventCaptor.getValue();
        PasswordResetEventPublisher.PasswordResetCompletedEventData data =
                (PasswordResetEventPublisher.PasswordResetCompletedEventData) capturedEvent.getData();

        assertFalse(data.isSuccess());
    }

    @Test
    @DisplayName("Debe manejar excepciones en publicación de reset requested sin lanzar error")
    void shouldHandleExceptionsInPasswordResetRequestedGracefully() {
        // Given
        doThrow(new RuntimeException("Redis connection error"))
                .when(redisTemplate).convertAndSend(anyString(), any());

        // When & Then
        assertDoesNotThrow(() ->
                eventPublisher.publishPasswordResetRequested(email, user, verificationCode)
        );
    }

    @Test
    @DisplayName("Debe manejar excepciones en publicación de reset verified sin lanzar error")
    void shouldHandleExceptionsInPasswordResetVerifiedGracefully() {
        // Given
        doThrow(new RuntimeException("Redis connection error"))
                .when(redisTemplate).convertAndSend(anyString(), any());

        // When & Then
        assertDoesNotThrow(() ->
                eventPublisher.publishPasswordResetVerified(email, verificationCode)
        );
    }

    @Test
    @DisplayName("Debe manejar excepciones en publicación de reset completed sin lanzar error")
    void shouldHandleExceptionsInPasswordResetCompletedGracefully() {
        // Given
        doThrow(new RuntimeException("Redis connection error"))
                .when(redisTemplate).convertAndSend(anyString(), any());

        // When & Then
        assertDoesNotThrow(() ->
                eventPublisher.publishPasswordResetCompleted(email, user, true)
        );
    }

    @Test
    @DisplayName("Debe generar eventId único en cada publicación")
    void shouldGenerateUniqueEventIdForEachPublication() {
        // When
        eventPublisher.publishPasswordResetRequested(email, user, verificationCode);
        eventPublisher.publishPasswordResetRequested(email, user, verificationCode);

        // Then
        verify(redisTemplate, times(2)).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        var events = eventCaptor.getAllValues();
        assertNotEquals(events.get(0).getEventId(), events.get(1).getEventId());
    }

    @Test
    @DisplayName("Debe usar el canal correcto de Redis para todos los eventos")
    void shouldUseCorrectRedisChannelForAllEvents() {
        // When
        eventPublisher.publishPasswordResetRequested(email, user, verificationCode);
        eventPublisher.publishPasswordResetVerified(email, verificationCode);
        eventPublisher.publishPasswordResetCompleted(email, user, true);

        // Then
        verify(redisTemplate, times(3)).convertAndSend(eq("events.password.reset"), any(EventWrapper.class));
    }

    @Test
    @DisplayName("Debe preservar todos los datos del usuario en evento requested")
    void shouldPreserveAllUserDataInRequestedEvent() {
        // Given
        User adminUser = new User(
                new UserId("admin-456"),
                new IdentityDocument("987654321"),
                new Email("admin@example.com"),
                new FullName("Admin User"),
                new PasswordHash("adminHash"),
                Role.ADMIN
        );

        // When
        eventPublisher.publishPasswordResetRequested(new Email("admin@example.com"), adminUser, "654321");

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        PasswordResetEventPublisher.PasswordResetRequestEventData data =
                (PasswordResetEventPublisher.PasswordResetRequestEventData) eventCaptor.getValue().getData();

        assertAll("Verify all user data is preserved",
                () -> assertEquals("admin@example.com", data.getEmail()),
                () -> assertEquals("admin-456", data.getUserId()),
                () -> assertEquals("Admin User", data.getName()),
                () -> assertEquals("654321", data.getVerificationCode())
        );
    }

    @Test
    @DisplayName("Debe incluir timestamp en formato ISO en todos los eventos")
    void shouldIncludeISOTimestampInAllEvents() {
        // When
        eventPublisher.publishPasswordResetRequested(email, user, verificationCode);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        String timestamp = eventCaptor.getValue().getTimestamp();
        assertNotNull(timestamp);
        assertFalse(timestamp.isEmpty());
        // Verificar formato ISO básico (contiene 'T')
        assertTrue(timestamp.contains("T"));
    }

    @Test
    @DisplayName("Debe manejar códigos de verificación con diferentes formatos")
    void shouldHandleVerificationCodesWithDifferentFormats() {
        // Given
        String alphanumericCode = "ABC123XYZ";

        // When
        eventPublisher.publishPasswordResetRequested(email, user, alphanumericCode);

        // Then
        verify(redisTemplate).convertAndSend(eq("events.password.reset"), eventCaptor.capture());

        PasswordResetEventPublisher.PasswordResetRequestEventData data =
                (PasswordResetEventPublisher.PasswordResetRequestEventData) eventCaptor.getValue().getData();

        assertEquals("ABC123XYZ", data.getVerificationCode());
    }
}
