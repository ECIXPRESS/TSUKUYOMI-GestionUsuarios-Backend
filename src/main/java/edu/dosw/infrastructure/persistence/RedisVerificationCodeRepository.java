package edu.dosw.infrastructure.persistence;

import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.model.ValueObject.VerificationCode;
import edu.dosw.domain.ports.VerificationCodeRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisVerificationCodeRepository implements VerificationCodeRepositoryPort {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "verification_code:";

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        String key = KEY_PREFIX + verificationCode.email();

        try {

            Map<String, Object> codeData = Map.of(
                    "code", verificationCode.code(),
                    "expiresAt", verificationCode.expiresAt().toString(),
                    "email", verificationCode.email(),
                    "used", verificationCode.used()
            );

            redisTemplate.opsForValue().set(key, codeData);

            long ttl = Duration.between(LocalDateTime.now(), verificationCode.expiresAt())
                    .toMinutes();
            redisTemplate.expire(key, Math.max(ttl, 1), TimeUnit.MINUTES);

            log.debug("Verification code saved for email: {}", verificationCode.email());
            return verificationCode;

        } catch (Exception e) {
            log.error("Error saving verification code for email: {}", verificationCode.email(), e);
            throw new RuntimeException("Failed to save verification code", e);
        }
    }

    @Override
    public Optional<VerificationCode> findByEmail(Email email) {
        String key = KEY_PREFIX + email.value();

        try {
            Map<String, Object> codeData = (Map<String, Object>) redisTemplate.opsForValue().get(key);

            if (codeData == null) {
                return Optional.empty();
            }

            VerificationCode verificationCode = new VerificationCode(
                    (String) codeData.get("code"),
                    LocalDateTime.parse((String) codeData.get("expiresAt")),
                    (String) codeData.get("email"),
                    (Boolean) codeData.get("used")
            );

            return Optional.of(verificationCode);

        } catch (Exception e) {
            log.error("Error retrieving verification code for email: {}", email.value(), e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteByEmail(Email email) {
        String key = KEY_PREFIX + email.value();

        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Verification code deleted for email: {}, success: {}", email.value(), deleted);

        } catch (Exception e) {
            log.error("Error deleting verification code for email: {}", email.value(), e);
        }
    }

    @Override
    public boolean existsByEmail(Email email) {
        String key = KEY_PREFIX + email.value();

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));

        } catch (Exception e) {
            log.error("Error checking verification code existence for email: {}", email.value(), e);
            return false;
        }
    }
}