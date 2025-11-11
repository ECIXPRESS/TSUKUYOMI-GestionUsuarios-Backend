package edu.dosw.domain.ports;

import edu.dosw.domain.model.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminRepository {
    Admin save(Admin admin);
    Optional<Admin> findByUserId(String userId);
    Optional<Admin> findByEmail(String email);
    List<Admin> findAll();
    boolean existsByEmail(String email);
    void delete(Admin admin);
}