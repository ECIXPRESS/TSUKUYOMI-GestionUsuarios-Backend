package edu.dosw.domain.ports;

import edu.dosw.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    boolean existsByEmail(String email);
    void deleteByUserId(String userId);
}