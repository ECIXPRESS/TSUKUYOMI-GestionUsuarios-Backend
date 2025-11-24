package edu.dosw.application.services;

import edu.dosw.application.ports.EventPublisherPort;
import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.model.ValueObject.PasswordHash;
import edu.dosw.domain.model.ValueObject.VerificationCode;
import edu.dosw.domain.ports.UserRepositoryPort;
import edu.dosw.domain.ports.VerificationCodeRepositoryPort;
import edu.dosw.domain.ports.PasswordEncoderPort;
import edu.dosw.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepositoryPort userRepository;
    private final VerificationCodeRepositoryPort verificationCodeRepository;
    private final EventPublisherPort eventPublisher;
    private final PasswordEncoderPort passwordEncoder;

    private static final int CODE_EXPIRY_MINUTES = 15;

    public void requestPasswordReset(Email email) {

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    String code = generateVerificationCode();
                    VerificationCode verificationCode = new VerificationCode(
                            code,
                            LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES),
                            email.value(),
                            false
                    );

                    verificationCodeRepository.save(verificationCode);
                    eventPublisher.publishPasswordResetRequested(email, user, code);

                    log.info("Password reset requested for email: {}, code: {}", email.value(), code);
                });

    }

    public void verifyCode(Email email, String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Verification code not found or expired"));

        if (!verificationCode.isValid(code)) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        VerificationCode usedCode = verificationCode.markAsUsed();
        verificationCodeRepository.save(usedCode);
        eventPublisher.publishPasswordResetVerified(email, code);

        log.info("Verification code validated for email: {}", email.value());
    }

    public void resetPassword(Email email, String code, String newPassword) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Verification code not found"));

        if (!verificationCode.isValid(code)) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(new PasswordHash(encodedPassword));

        userRepository.save(user);

        verificationCodeRepository.deleteByEmail(email);
        eventPublisher.publishPasswordResetCompleted(email, user, true);

        log.info("Password reset completed for email: {}", email.value());
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}