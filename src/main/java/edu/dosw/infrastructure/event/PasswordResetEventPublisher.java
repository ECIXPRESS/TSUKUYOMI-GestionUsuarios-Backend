package edu.dosw.infrastructure.event;

import edu.dosw.application.ports.EventPublisherPort;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetEventPublisher implements EventPublisherPort {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publishPasswordResetRequested(Email email, User user, String verificationCode) {
        try {
            var eventWrapper = new EventWrapper();
            eventWrapper.setEventId(UUID.randomUUID().toString());
            eventWrapper.setEventType("password.reset.requested");
            eventWrapper.setTimestamp(Instant.now().toString());
            eventWrapper.setVersion("1.0");

            var eventData = new PasswordResetRequestEventData();
            eventData.setEmail(email.value());
            eventData.setUserId(user.getUserId().value());
            eventData.setName(user.getFullName().value());
            eventData.setVerificationCode(verificationCode);

            eventWrapper.setData(eventData);

            redisTemplate.convertAndSend("events.password.reset", eventWrapper);
            log.info("Password reset requested event published for email: {}", email.value());

        } catch (Exception e) {
            log.error("Error publishing password reset requested event: {}", e.getMessage(), e);
        }
    }

    @Override
    public void publishPasswordResetVerified(Email email, String verificationCode) {
        try {
            var eventWrapper = new EventWrapper();
            eventWrapper.setEventId(UUID.randomUUID().toString());
            eventWrapper.setEventType("password.reset.verified");
            eventWrapper.setTimestamp(Instant.now().toString());
            eventWrapper.setVersion("1.0");

            var eventData = new PasswordResetVerifiedEventData();
            eventData.setEmail(email.value());
            eventData.setVerificationCode(verificationCode);

            eventWrapper.setData(eventData);

            redisTemplate.convertAndSend("events.password.reset", eventWrapper);
            log.info("Password reset verified event published for email: {}", email.value());

        } catch (Exception e) {
            log.error("Error publishing password reset verified event: {}", e.getMessage(), e);
        }
    }

    @Override
    public void publishPasswordResetCompleted(Email email, User user, boolean success) {
        try {
            var eventWrapper = new EventWrapper();
            eventWrapper.setEventId(UUID.randomUUID().toString());
            eventWrapper.setEventType("password.reset.completed");
            eventWrapper.setTimestamp(Instant.now().toString());
            eventWrapper.setVersion("1.0");

            var eventData = new PasswordResetCompletedEventData();
            eventData.setEmail(email.value());
            eventData.setUserId(user.getUserId().value());
            eventData.setName(user.getFullName().value()); // Usar value() de FullName
            eventData.setSuccess(success);

            eventWrapper.setData(eventData);

            redisTemplate.convertAndSend("events.password.reset", eventWrapper);
            log.info("Password reset completed event published for email: {}", email.value());

        } catch (Exception e) {
            log.error("Error publishing password reset completed event: {}", e.getMessage(), e);
        }
    }

    @lombok.Data
    public static class PasswordResetRequestEventData {
        private String email;
        private String userId;
        private String name;
        private String verificationCode;
    }

    @lombok.Data
    public static class PasswordResetVerifiedEventData {
        private String email;
        private String verificationCode;
    }

    @lombok.Data
    public static class PasswordResetCompletedEventData {
        private String email;
        private String userId;
        private String name;
        private boolean success;
    }
}