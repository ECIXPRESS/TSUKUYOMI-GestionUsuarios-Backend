package edu.dosw.application.ports;

import edu.dosw.domain.model.ValueObject.Email;
import edu.dosw.domain.model.User;

public interface EventPublisherPort {
    void publishPasswordResetRequested(Email email, User user, String verificationCode);
    void publishPasswordResetVerified(Email email, String verificationCode);
    void publishPasswordResetCompleted(Email email, User user, boolean success);
}