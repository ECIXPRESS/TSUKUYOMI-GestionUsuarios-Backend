package edu.dosw.domain.ports;

import edu.dosw.domain.model.User;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUserId(UserId userId);
    boolean existsByEmail(Email email);
    void deleteByUserId(UserId userId);
}