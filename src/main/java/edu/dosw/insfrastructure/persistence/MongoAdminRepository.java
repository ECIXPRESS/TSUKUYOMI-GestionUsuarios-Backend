package edu.dosw.insfrastructure.persistence;

import edu.dosw.domain.model.Admin;
import edu.dosw.domain.ports.AdminRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MongoAdminRepository extends MongoRepository<Admin, String>, AdminRepository {
    @Query("{ 'email': ?0 }")
    Optional<Admin> findByEmail(String email);

    @Query("{ 'userId': ?0 }")
    Optional<Admin> findByUserId(String userId);

    @Query(value = "{ 'email': ?0 }", exists = true)
    boolean existsByEmail(String email);
}