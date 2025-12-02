package edu.dosw.domain.ports;

import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.model.ValueObject.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepositoryPort {
    VerificationCode save(VerificationCode verificationCode);
    Optional<VerificationCode> findByEmail(Email email);
    void deleteByEmail(Email email);
    boolean existsByEmail(Email email);
}