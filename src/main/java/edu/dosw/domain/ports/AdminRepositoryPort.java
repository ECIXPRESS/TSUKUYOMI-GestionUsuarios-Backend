package edu.dosw.domain.ports;

import edu.dosw.domain.model.Admin;
import edu.dosw.domain.model.ValueObject.UserId;
import edu.dosw.domain.model.ValueObject.Email;
import java.util.List;
import java.util.Optional;

public interface AdminRepositoryPort {
    Admin save(Admin admin);
    Optional<Admin> findByUserId(UserId userId);
    Optional<Admin> findByEmail(Email email);
    List<Admin> findAll();
    boolean existsByEmail(Email email);
    void delete(Admin admin);
}

